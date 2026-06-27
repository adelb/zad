# زاد · Zad

Android app: photograph a meal, get the dish + estimated grams + calories in Arabic, add it to today's Breakfast / Lunch / Dinner / Snack, see daily totals and history. Local-only.

## Build

1. Open the folder in **Android Studio Hedgehog (or newer)**.
2. Let it sync — Gradle will pull the Compose BOM, CameraX, Room, TFLite, and the downloadable Google Fonts (Aref Ruqaa + Cairo) used for the Arabic typography.
3. Run on a real device (camera required). Minimum API 26.

## Food classifier model

Drop a TFLite image-classification model + label list into `app/src/main/assets/`:

- `food_classifier.tflite` — 224×224 RGB input, softmax/logit output over N classes.
- `labels.txt` — one label per line, each label matching a `Dish.id` in [`DishCatalog.kt`](app/src/main/kotlin/com/zad/app/ml/DishCatalog.kt) (e.g. `mansaf`, `kabsa`, `shawarma_chicken`, …).

If no model is present the app still works — the result screen opens with the unknown placeholder and the user picks the dish from the catalog manually.

## How the weight estimation works

1. The user puts a credit/ID card (ISO/IEC 7810 ID-1 — 85.60 × 53.98 mm) on the table next to the plate and shoots a photo.
2. After classification, the user taps **قياس دقيق بالبطاقة** (precise card scaling), taps the four corners of the card, then drags a rectangle around the dish.
3. From the card corners the app computes mm/pixel; from the rectangle it computes plate area in cm² and combines it with the dish's typical thickness + bulk density to estimate grams.
4. Grams × `kcalPer100g` ÷ 100 = calories. The user can fine-tune with the grams slider.

If the user skips scaling, the dish's typical portion is used as a sensible default.

## Storage

Room (SQLite) on-device only — see [`ZadDatabase.kt`](app/src/main/kotlin/com/zad/app/data/ZadDatabase.kt). Day-key strings (`yyyy-MM-dd`) drive per-day grouping; meals are bucketed into `MealType` ∈ {Breakfast, Lunch, Dinner, Snack}.
