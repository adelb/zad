package com.zad.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zad.app.R

/**
 * Fonts shipped inside the APK — no Google Fonts provider, no network on first launch.
 * - Aref Ruqaa Regular + Bold for display / headlines / titles / labels
 * - Cairo (variable) for body text
 */
val Ruqaa = FontFamily(
    Font(R.font.aref_ruqaa_regular, FontWeight.Normal),
    Font(R.font.aref_ruqaa_bold,    FontWeight.Bold)
)

val Body = FontFamily(
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_regular, FontWeight.Medium),
    Font(R.font.cairo_regular, FontWeight.SemiBold),
    Font(R.font.cairo_regular, FontWeight.Bold)
)

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
