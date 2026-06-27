package com.zad.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.zad.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val ArefRuqaa = GoogleFont("Aref Ruqaa")
private val Cairo = GoogleFont("Cairo")

val Ruqaa = FontFamily(
    Font(googleFont = ArefRuqaa, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = ArefRuqaa, fontProvider = provider, weight = FontWeight.Bold)
)

val Body = FontFamily(
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Bold)
)

/**
 * Ruqaa dominates: display, headlines, titles, and button labels.
 * Cairo carries body text, where Ruqaa would slow reading.
 */
val ZadTypography = Typography(
    displayLarge  = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 60.sp),
    displayMedium = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 48.sp),
    displaySmall  = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 40.sp),

    headlineLarge = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 34.sp),
    headlineMedium= TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
    headlineSmall = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 28.sp),

    titleLarge    = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold,    fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Normal,  fontSize = 17.sp, lineHeight = 26.sp),
    titleSmall    = TextStyle(fontFamily = Body,  fontWeight = FontWeight.SemiBold,fontSize = 14.sp, lineHeight = 22.sp),

    bodyLarge     = TextStyle(fontFamily = Body, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium    = TextStyle(fontFamily = Body, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall     = TextStyle(fontFamily = Body, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 18.sp),

    labelLarge    = TextStyle(fontFamily = Ruqaa, fontWeight = FontWeight.Bold,    fontSize = 16.sp, lineHeight = 22.sp),
    labelMedium   = TextStyle(fontFamily = Body,  fontWeight = FontWeight.SemiBold,fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontFamily = Body,  fontWeight = FontWeight.SemiBold,fontSize = 11.sp, lineHeight = 16.sp)
)
