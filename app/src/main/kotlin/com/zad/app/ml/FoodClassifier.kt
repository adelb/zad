package com.zad.app.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.exp

data class Prediction(val dishId: String, val nameAr: String, val confidence: Float)

/**
 * Classifies a photo of food.
 *
 * Looks for `assets/food_classifier.tflite` + `assets/labels.txt` (one label per line,
 * each label matching a [Dish.id] in [DishCatalog]). If the model file is missing
 * (e.g. before the user drops in a trained model), classification falls back to
 * returning [DishCatalog.UNKNOWN] with low confidence so the rest of the flow still
 * works — the user can pick the dish manually.
 */
class FoodClassifier(private val context: Context) {

    companion object {
        private const val MODEL_FILE = "food_classifier.tflite"
        private const val LABELS_FILE = "labels.txt"
        private const val INPUT_SIZE = 224
        const val LOW_CONFIDENCE_THRESHOLD = 0.55f
    }

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    val isReady: Boolean get() = interpreter != null && labels.isNotEmpty()

    fun load() {
        if (isReady) return
        try {
            val model = FileUtil.loadMappedFile(context, MODEL_FILE)
            interpreter = Interpreter(model)
            labels = FileUtil.loadLabels(context, LABELS_FILE)
        } catch (_: IOException) {
            // Model not shipped yet — classifier will return UNKNOWN.
            interpreter = null
            labels = emptyList()
        }
    }

    fun classify(bitmap: Bitmap): Prediction {
        load()
        val interp = interpreter ?: return Prediction(
            dishId = DishCatalog.UNKNOWN.id,
            nameAr = DishCatalog.UNKNOWN.nameAr,
            confidence = 0f
        )

        val input = TensorImage.fromBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, false))
            .let(imageProcessor::process)
        val outputShape = interp.getOutputTensor(0).shape()
        val numClasses = outputShape[outputShape.size - 1]
        val output = Array(1) { FloatArray(numClasses) }
        interp.run(input.buffer.rewindAsBuffer(), output)

        val probs = softmaxIfNeeded(output[0])
        val bestIdx = probs.indices.maxByOrNull { probs[it] } ?: return Prediction(
            DishCatalog.UNKNOWN.id, DishCatalog.UNKNOWN.nameAr, 0f
        )
        val labelId = labels.getOrNull(bestIdx) ?: DishCatalog.UNKNOWN.id
        val dish = DishCatalog.byId(labelId) ?: DishCatalog.UNKNOWN
        return Prediction(dish.id, dish.nameAr, probs[bestIdx])
    }

    private fun ByteBuffer.rewindAsBuffer(): ByteBuffer { rewind(); return this }

    private fun softmaxIfNeeded(arr: FloatArray): FloatArray {
        val sum = arr.sum()
        if (sum in 0.99f..1.01f && arr.all { it in 0f..1f }) return arr
        val max = arr.max()
        val exps = FloatArray(arr.size) { exp((arr[it] - max).toDouble()).toFloat() }
        val s = exps.sum()
        return FloatArray(arr.size) { exps[it] / s }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
