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

data class Prediction(
    val dishId: String,
    val nameAr: String,
    val confidence: Float,
    /** The raw English label ML Kit returned. Shown so the user can see what was detected. */
    val rawLabel: String? = null
)

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
        const val LOW_CONFIDENCE_THRESHOLD = 0.40f
        private const val MIN_LABEL_CONFIDENCE = 0.20f
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
            return Prediction(dish.id, dish.nameAr, l.confidence, rawLabel = l.text)
        }

        // Got labels but none matched our catalog — keep the top confidence
        // for the "this looks like food but I'm not sure what" hint.
        val top = sorted.first()
        return Prediction(
            dishId = DishCatalog.UNKNOWN.id,
            nameAr = DishCatalog.UNKNOWN.nameAr,
            confidence = top.confidence.coerceAtMost(0.5f),
            rawLabel = top.text
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
        // ── Specific dishes ──
        Regex("(?i)falafel")                           to "falafel",
        Regex("(?i)shawarma|gyro|kebab wrap")          to "shawarma_chicken",
        Regex("(?i)hummus|chickpea")                   to "hummus",
        Regex("(?i)tabbouleh|tabouleh|parsley salad")  to "tabbouleh",
        Regex("(?i)fattoush")                          to "fattoush",
        Regex("(?i)kebab|skewer|köfte|kofta")          to "kebab",
        Regex("(?i)pizza")                             to "pizza_slice",
        Regex("(?i)burger|hamburger|cheeseburger")     to "burger_beef",
        Regex("(?i)fries|french fr|chips|wedges|potato fr") to "french_fries",
        Regex("(?i)pasta|spaghetti|macaroni|noodle|ramen|udon|lasagn") to "pasta_tomato",
        Regex("(?i)omelette|scrambled|fried egg|frittata|egg") to "egg_omelette",
        Regex("(?i)labneh|yogurt|cheese|feta|halloumi|labna") to "yogurt_labneh",
        Regex("(?i)kanafeh|knafeh|baklava|halva|pastry|cake|donut|cookie|biscuit|brownie|tart|pie|dessert|sweet") to "kanafeh",
        Regex("(?i)mansaf")                            to "mansaf",
        Regex("(?i)kabsa|biryani|paella")              to "kabsa",
        Regex("(?i)maqluba|maqloubeh")                 to "maqluba",
        Regex("(?i)mulukhiyah|molokhia")               to "mulukhiyah",
        Regex("(?i)fatteh|fattah")                     to "fattah",
        Regex("(?i)kibbeh|kibbe")                      to "kibbeh",
        Regex("(?i)mahshi|mahashi|stuffed (vegetable|pepper|zucchini)") to "mahshi",
        Regex("(?i)warak enab|vine leaves|dolma")      to "warak_enab",
        Regex("(?i)foul|ful|fava")                     to "foul_mudammas",
        Regex("(?i)musakhan")                          to "musakhan",
        Regex("(?i)shish tawook|shish taouk")          to "shish_tawook",
        Regex("(?i)mujadara|mujaddara|jadara")         to "mujadara",
        Regex("(?i)dates? \\b|tamr")                   to "dates",

        // ── Broader buckets ──
        Regex("(?i)rice|risotto|pilaf")                                  to "rice_white",
        Regex("(?i)bread|naan|pita|loaf|toast|baguette|bun|roll|sandwich") to "bread_pita",
        Regex("(?i)salad|vegetable|greens|lettuce|cucumber|tomato")        to "salad_green",
        Regex("(?i)beef|steak|lamb|brisket|ribs|meat\\b|barbec|grill|bbq") to "mashawi",
        Regex("(?i)chicken|poultry|wing|drumstick|breast")                 to "shish_tawook",
        Regex("(?i)fish|seafood|shrimp|prawn|salmon|tuna|sardine")         to "mashawi",
        Regex("(?i)soup|stew|curry|broth|chowder|gumbo")                   to "mulukhiyah",
        Regex("(?i)bean|lentil|legume|peas?\\b")                           to "foul_mudammas",
        Regex("(?i)nut|almond|cashew|pistachio|walnut|peanut")             to "nuts_mixed",
        Regex("(?i)apple")                                                 to "fruit_apple",
        Regex("(?i)banana")                                                to "fruit_banana",
        Regex("(?i)orange|tangerine|clementine")                           to "fruit_orange",
        Regex("(?i)juice|smoothie|lemonade")                               to "juice_orange",
        Regex("(?i)\\btea\\b|chai|matcha")                                 to "tea_with_sugar",
        Regex("(?i)coffee|espresso|cappuccino|latte|mocha")                to "coffee_arabic",

        // Very generic "food" / "cuisine" / "meal" — fall through to a sensible default
        Regex("(?i)cuisine|meal|breakfast|lunch|dinner|food|dish")         to "kabsa"
    )

    fun lookup(label: String): String? {
        for ((re, id) in map) if (re.containsMatchIn(label)) return id
        return null
    }
}
