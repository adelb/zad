package com.zad.app.ui

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zad.app.data.ExerciseSet
import com.zad.app.data.MealEntry
import com.zad.app.data.MealRepository
import com.zad.app.data.MealTotal
import com.zad.app.data.MealType
import com.zad.app.data.Profile
import com.zad.app.data.ProfileStore
import com.zad.app.data.Routine
import com.zad.app.data.RoutineExercise
import com.zad.app.data.TrackingRepository
import com.zad.app.data.WeightEntry
import com.zad.app.data.WorkoutRepository
import com.zad.app.data.WorkoutSession
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

    private val db = ZadDatabase.get(app)
    private val repo: MealRepository = MealRepository(db.mealDao())
    private val workoutRepo = WorkoutRepository(db.workoutDao())
    private val tracking = TrackingRepository(db.weightDao(), db.waterDao())
    private val classifier = FoodClassifier(app)
    private val profileStore = ProfileStore(app)

    val onboarded: StateFlow<Boolean?> = profileStore.onboarded
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val profile: StateFlow<Profile?> = profileStore.profile
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun saveProfile(p: Profile) {
        viewModelScope.launch { profileStore.save(p) }
    }

    // ── Workouts ──
    val routines: StateFlow<List<Routine>> = workoutRepo.routines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<WorkoutSession>> = workoutRepo.recentSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun exercisesForRoutine(routineId: Long) = workoutRepo.exercisesForRoutine(routineId)
    fun setsForSession(sessionId: Long) = workoutRepo.setsForSession(sessionId)
    fun topWeightPerSession(exerciseId: String) = workoutRepo.topWeightPerSession(exerciseId)

    suspend fun startSession(routineId: Long?, name: String): Long =
        workoutRepo.startSession(routineId, name)

    fun logSet(sessionId: Long, exerciseId: String, nameAr: String,
               setNumber: Int, weightKg: Double, reps: Int) {
        viewModelScope.launch {
            workoutRepo.logSet(sessionId, exerciseId, nameAr, setNumber, weightKg, reps)
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch { workoutRepo.deleteSet(setId) }
    }

    suspend fun createCustomRoutine(name: String, description: String,
                                    exercises: List<Triple<String, Int, Int>>): Long =
        workoutRepo.createCustomRoutine(name, description, exercises)

    fun deleteCustomRoutine(id: Long) {
        viewModelScope.launch { workoutRepo.deleteCustomRoutine(id) }
    }

    // ── Body weight ──
    val recentWeights: StateFlow<List<WeightEntry>> = tracking.recentWeights()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestWeight: StateFlow<WeightEntry?> = tracking.latestWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logBodyWeight(kg: Double) {
        viewModelScope.launch { tracking.logWeight(kg) }
    }

    // ── Water ──
    val todayWaterMl: StateFlow<Int> = tracking.todayWaterMl()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addWater(ml: Int) {
        viewModelScope.launch { tracking.addWater(ml) }
    }

    fun resetTodayWater() {
        viewModelScope.launch { tracking.clearTodayWater() }
    }

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
