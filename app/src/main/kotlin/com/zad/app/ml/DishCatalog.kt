package com.zad.app.ml

/**
 * Nutrition + geometry parameters per dish.
 *
 * @param id            Stable id, matches classifier label.
 * @param nameAr        Arabic display name.
 * @param kcalPer100g   Calories per 100 g, from published nutrition tables.
 * @param densityGPerCm3 Bulk density of the served dish (g/cm³), used to convert
 *                       estimated volume → grams when the user scales with a card.
 * @param typicalThicknessCm Average plate height; multiplied by measured area for
 *                       volume estimation when the user outlines the dish.
 * @param typicalGrams  Default portion size used when the user skips scaling.
 */
data class Dish(
    val id: String,
    val nameAr: String,
    val kcalPer100g: Int,
    val densityGPerCm3: Double,
    val typicalThicknessCm: Double,
    val typicalGrams: Int
) {
    fun caloriesFor(grams: Int): Int = (grams * kcalPer100g / 100.0).toInt()
}

/**
 * Catalog of common Arab + everyday dishes. kcal values are from standard
 * USDA / regional nutrition references; densities are realistic averages.
 * Tune as needed — every dish has a sensible default so the app always
 * returns something useful even before a trained model is shipped.
 */
object DishCatalog {

    val ALL: List<Dish> = listOf(
        Dish("mansaf",        "منسف",         170, 0.90, 4.0, 450),
        Dish("kabsa",         "كبسة",         165, 0.85, 3.5, 400),
        Dish("maqluba",       "مقلوبة",       150, 0.85, 3.5, 380),
        Dish("mujadara",      "مجدّرة",       140, 0.95, 2.5, 300),
        Dish("mulukhiyah",    "ملوخية",       100,  1.0, 2.0, 280),
        Dish("fattah",        "فتّة",          180, 0.85, 3.0, 320),
        Dish("hummus",        "حمّص",          165, 1.10, 1.5, 150),
        Dish("tabbouleh",     "تبّولة",        120, 0.70, 2.5, 180),
        Dish("fattoush",      "فتّوش",         110, 0.65, 3.0, 180),
        Dish("shawarma_chicken","شاورما دجاج", 230, 0.95, 3.0, 300),
        Dish("shawarma_beef", "شاورما لحم",   260, 0.95, 3.0, 300),
        Dish("falafel",       "فلافل",        330, 1.00, 2.0, 120),
        Dish("kanafeh",       "كنافة",        320, 1.10, 2.5, 180),
        Dish("kebab",         "كباب",         240, 1.05, 2.0, 200),
        Dish("mashawi",       "مشاوي مشكّلة", 230, 1.05, 2.0, 280),
        Dish("kibbeh",        "كبّة",          290, 1.00, 2.5, 150),
        Dish("warak_enab",    "ورق عنب",      160, 1.00, 2.0, 200),
        Dish("mahshi",        "محشي",         170, 1.00, 3.0, 280),
        Dish("shish_tawook",  "شيش طاووق",    180, 1.00, 2.0, 250),
        Dish("foul_mudammas", "فول مدمّس",     140, 1.05, 2.0, 220),
        Dish("musakhan",      "مسخّن",         260, 0.95, 2.5, 320),
        Dish("zalabia",       "زلابية",        360, 1.00, 2.0,  90),
        Dish("makloubeh_chicken","مقلوبة دجاج",160, 0.90, 3.5, 400),
        Dish("rice_white",    "أرز أبيض",     130, 0.90, 2.5, 200),
        Dish("bread_pita",    "خبز عربي",     270, 0.45, 0.5,  60),
        Dish("salad_green",   "سلطة خضراء",    35, 0.50, 3.0, 150),
        Dish("yogurt_labneh", "لبنة",         150, 1.05, 1.0,  80),
        Dish("egg_omelette",  "عجّة بيض",      155, 1.00, 1.5, 120),
        Dish("french_fries",  "بطاطا مقلية",  310, 0.55, 2.0, 150),
        Dish("burger_beef",   "برغر لحم",     260, 0.85, 4.0, 220),
        Dish("pizza_slice",   "قطعة بيتزا",   270, 0.70, 1.5, 130),
        Dish("pasta_tomato",  "معكرونة بطماطم",150, 0.80, 2.5, 250),
        Dish("fruit_apple",   "تفّاحة",         52, 0.85, 6.0, 180),
        Dish("fruit_banana",  "موزة",           89, 0.95, 3.0, 120),
        Dish("fruit_orange",  "برتقالة",        47, 0.90, 6.0, 200),
        Dish("dates",         "تمر",           280, 1.30, 2.0,  40),
        Dish("nuts_mixed",    "مكسّرات",        600, 0.60, 2.0,  30),
        Dish("tea_with_sugar","شاي بسكّر",      30, 1.00, 0.0, 200),
        Dish("coffee_arabic", "قهوة عربية",     10, 1.00, 0.0, 100),
        Dish("juice_orange",  "عصير برتقال",    45, 1.05, 0.0, 250)
    )

    private val byId = ALL.associateBy { it.id }
    fun byId(id: String): Dish? = byId[id]

    val UNKNOWN = Dish("unknown", "طبق", 150, 0.90, 2.5, 250)
}
