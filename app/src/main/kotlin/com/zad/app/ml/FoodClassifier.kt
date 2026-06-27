package com.zad.app.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Prediction(val dishId: String, val nameAr: String, val confidence: Float)

/**
 * Classifies a photo of food using ML Kit's on-device image labeler
 * (a quantized MobileNet shipped with Play Services). The labeler returns
 * generic English labels like "Food", "Rice", "Bread", "Salad"; we map
 * those to a [Dish] in [DishCatalog] via a keyword table.
 *
 * If nothing matches, we still return [DishCatalog.UNKNOWN] so the result
 * screen opens normally and the user picks the dish manually — that's the
 * same fallback the camera-only flow always has.
 *
 * No `.tflite` file to ship. Works offline after the first launch.
 */
class FoodClassifier(@Suppress("unused") context: Context) {

    companion object {
        const val LOW_CONFIDENCE_THRESHOLD = 0.55f
        private const val MIN_LABEL_CONFIDENCE = 0.50f
    }

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(MIN_LABEL_CONFIDENCE)
            .build()
    )

    suspend fun classifySuspend(bitmap: Bitmap): Prediction {
        val labels = runLabeler(bitmap)
        if (labels.isEmpty()) return Prediction(DishCatalog.UNKNOWN.id, DishCatalog.UNKNOWN.nameAr, 0f)

        // Walk labels in confidence order; first one that maps to a dish wins.
        val sorted = labels.sortedByDescending { it.confidence }
        for (l in sorted) {
            val dishId = LabelToDish.lookup(l.text) ?: continue
            val dish = DishCatalog.byId(dishId) ?: continue
            return Prediction(dish.id, dish.nameAr, l.confidence)
        }

        // Got labels but none matched our catalog — keep the top confidence
        // for the "this looks like food but I'm not sure what" hint.
        return Prediction(
            dishId = DishCatalog.UNKNOWN.id,
            nameAr = DishCatalog.UNKNOWN.nameAr,
            confidence = sorted.first().confidence.coerceAtMost(0.5f)
        )
    }

    private suspend fun runLabeler(bitmap: Bitmap): List<ImageLabel> =
        suspendCancellableCoroutine { cont ->
            val input = InputImage.fromBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, false), 0)
            labeler.process(input)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    fun close() = labeler.close()
}

/**
 * Maps ML Kit's generic English labels onto our Arab-dish catalog.
 *
 * ML Kit's default labeler is trained on Open Images / general categories,
 * so it won't say "kabsa" — it'll say "rice", "meat", "salad", "bread".
 * We pick the most specific match we can and let the user override.
 */
private object LabelToDish {

    private val map: List<Pair<Regex, String>> = listOf(
        // Specific dishes
        Regex("(?i)falafel")           to "falafel",
        Regex("(?i)shawarma")          to "shawarma_chicken",
        Regex("(?i)hummus|chickpea")   to "hummus",
        Regex("(?i)kebab|skewer")      to "kebab",
        Regex("(?i)pizza")             to "pizza_slice",
        Regex("(?i)burger|hamburger")  to "burger_beef",
        Regex("(?i)fries|french fr")   to "french_fries",
        Regex("(?i)pasta|spaghetti|noodle") to "pasta_tomato",
        Regex("(?i)omelette|scrambled|fried egg|egg") to "egg_omelette",
        Regex("(?i)yogurt|labneh|cheese") to "yogurt_labneh",
        Regex("(?i)kanafeh|knafeh|dessert|sweet|pastry|cake|donut") to "kanafia_fallback",

        // Categories — broader buckets the catalog can answer
        Regex("(?i)rice")              to "rice_white",
        Regex("(?i)bread|naan|pita|loaf") to "bread_pita",
        Regex("(?i)salad|vegetable|greens|lettuce|cucumber|tomato") to "salad_green",
        Regex("(?i)meat|steak|beef|lamb|grill") to "mashawi",
        Regex("(?i)chicken|poultry") to "shish_tawook",
        Regex("(?i)fish|seafood") to "mashawi",
        Regex("(?i)soup|stew|curry|broth") to "mulukhiyah",
        Regex("(?i)bean|lentil|legume") to "foul_mudammas",
        Regex("(?i)nut|almond|cashew|pistachio") to "nuts_mixed",
        Regex("(?i)date|fruit|apple|banana|orange") to "fruit_apple",
        Regex("(?i)juice|drink|beverage") to "juice_orange",
        Regex("(?i)tea") to "tea_with_sugar",
        Regex("(?i)coffee|espresso|cappuccino") to "coffee_arabic"
    )

    fun lookup(label: String): String? {
        for ((re, id) in map) if (re.containsMatchIn(label)) {
            // Map kanafia_fallback → kanafeh (we kept a friendlier ID)
            return if (id == "kanafia_fallback") "kanafeh" else id
        }
        return null
    }
}
