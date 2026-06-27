package com.zad.app.ui

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zad.app.data.MealEntry
import com.zad.app.data.MealRepository
import com.zad.app.data.MealTotal
import com.zad.app.data.MealType
import com.zad.app.data.ZadDatabase
import com.zad.app.ml.Dish
import com.zad.app.ml.DishCatalog
import com.zad.app.ml.FoodClassifier
import com.zad.app.ml.PortionEstimator
import com.zad.app.ml.Prediction
import com.zad.app.util.BitmapUtils
import com.zad.app.vision.computeScaleMmPerPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ScanState(
    val photoPath: String? = null,
    val prediction: Prediction? = null,
    val dish: Dish = DishCatalog.UNKNOWN,
    val grams: Int = DishCatalog.UNKNOWN.typicalGrams,
    val calories: Int = DishCatalog.UNKNOWN.caloriesFor(DishCatalog.UNKNOWN.typicalGrams),
    val mmPerPx: Double? = null,
    val savedSuccessfully: Boolean = false,
    val processing: Boolean = false
)

class ZadViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: MealRepository = MealRepository(ZadDatabase.get(app).mealDao())
    private val classifier = FoodClassifier(app)

    private val _scan = MutableStateFlow(ScanState())
    val scan: StateFlow<ScanState> = _scan.asStateFlow()

    val todayTotal: StateFlow<Int> = repo.totalToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayEntries: StateFlow<List<MealEntry>> = repo.entriesToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perMealToday: StateFlow<List<MealTotal>> = repo.perMealToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<MealEntry>> = repo.recent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Called once we have a photo path. Runs classifier off the main thread. */
    fun onPhotoReady(path: String) {
        viewModelScope.launch {
            _scan.update { it.copy(photoPath = path, processing = true, savedSuccessfully = false) }
            val bmp = withContext(Dispatchers.Default) {
                BitmapUtils.decodeOriented(path, maxDim = 1024)
            }
            val pred = if (bmp != null) {
                runCatching { classifier.classifySuspend(bmp) }.getOrNull()
            } else null
            val dish = pred?.dishId?.let(DishCatalog::byId) ?: DishCatalog.UNKNOWN
            val (g, kcal) = PortionEstimator.typical(dish)
            _scan.update {
                it.copy(
                    prediction = pred,
                    dish = dish,
                    grams = g,
                    calories = kcal,
                    processing = false
                )
            }
        }
    }

    fun pickDish(dishId: String) {
        val dish = DishCatalog.byId(dishId) ?: DishCatalog.UNKNOWN
        val (g, kcal) = PortionEstimator.forGrams(dish, _scan.value.grams)
        _scan.update { it.copy(dish = dish, grams = g, calories = kcal) }
    }

    fun setGrams(grams: Int) {
        val (g, kcal) = PortionEstimator.forGrams(_scan.value.dish, grams)
        _scan.update { it.copy(grams = g, calories = kcal) }
    }

    /** Apply 4-corner card calibration + plate rectangle to recompute grams. */
    fun applyScale(corners: List<androidx.compose.ui.geometry.Offset>, plateWidthPx: Float, plateHeightPx: Float) {
        val mmPerPx = computeScaleMmPerPx(corners) ?: return
        val areaCm2 = com.zad.app.ml.pixelAreaToCm2(plateWidthPx, plateHeightPx, mmPerPx)
        val (g, kcal) = PortionEstimator.fromArea(_scan.value.dish, areaCm2)
        _scan.update { it.copy(mmPerPx = mmPerPx, grams = g, calories = kcal) }
    }

    fun addToMeal(mealType: MealType) {
        val s = _scan.value
        viewModelScope.launch {
            val thumbPath = s.photoPath?.let { saveThumb(it) }
            repo.addEntry(
                mealType = mealType,
                dishId = s.dish.id,
                dishNameAr = s.dish.nameAr,
                grams = s.grams,
                calories = s.calories,
                photoPath = thumbPath
            )
            _scan.update { it.copy(savedSuccessfully = true) }
        }
    }

    fun reset() {
        _scan.value = ScanState()
    }

    fun deleteEntry(entry: MealEntry) {
        viewModelScope.launch { repo.delete(entry) }
    }

    private suspend fun saveThumb(originalPath: String): String? = withContext(Dispatchers.IO) {
        val bmp = BitmapFactory.decodeFile(originalPath) ?: return@withContext null
        val scale = 320f / maxOf(bmp.width, bmp.height)
        val w = (bmp.width * scale).toInt().coerceAtLeast(1)
        val h = (bmp.height * scale).toInt().coerceAtLeast(1)
        val resized = android.graphics.Bitmap.createScaledBitmap(bmp, w, h, true)
        BitmapUtils.persistThumbnail(getApplication(), resized)
    }

    override fun onCleared() {
        classifier.close()
        super.onCleared()
    }

    companion object {
        // Unused; left for future SavedStateHandle wiring.
        fun savedHandleStub(): SavedStateHandle = SavedStateHandle()
    }
}
