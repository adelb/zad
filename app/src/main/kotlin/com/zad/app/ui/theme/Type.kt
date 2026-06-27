package com.zad.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.zad.app.R

// Downloadable Google Fonts: Aref Ruqaa (display / classical Arabic) + Cairo (body)
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val ArefRuqaa = GoogleFont("Aref Ruqaa")
private val Cairo = GoogleFont("Cairo")

val DisplayFamily = FontFamily(
    Font(googleFont = ArefRuqaa, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = ArefRuqaa, fontProvider = provider, weight = FontWeight.Bold)
)

val BodyFamily = FontFamily(
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = Cairo, fontProvider = provider, weight = FontWeight.Bold)
)

val ZadTypography = Typography(
    displayLarge  = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,  fontSize = 44.sp, lineHeight = 56.sp),
    displayMedium = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,  fontSize = 34.sp, lineHeight = 44.sp),
    displaySmall  = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,  fontSize = 26.sp, lineHeight = 34.sp),
    headlineLarge = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,  fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium= TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,  fontSize = 20.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleLarge    = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleMedium   = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall    = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 22.sp),
    bodyLarge     = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium    = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall     = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge    = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium   = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontFamily = BodyFamily,    fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp)
)
