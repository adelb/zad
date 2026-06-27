package com.zad.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val ZadLight = lightColorScheme(
    primary = Date500,
    onPrimary = Sand50,
    primaryContainer = Sand200,
    onPrimaryContainer = Date700,
    secondary = Olive500,
    onSecondary = Sand50,
    secondaryContainer = Sand100,
    onSecondaryContainer = Olive700,
    tertiary = Saffron500,
    onTertiary = Sand50,
    tertiaryContainer = Sand100,
    onTertiaryContainer = Saffron700,
    background = Paper,
    onBackground = Ink900,
    surface = Paper,
    onSurface = Ink900,
    surfaceVariant = Sand100,
    onSurfaceVariant = Ink700,
    outline = Sand200,
    outlineVariant = Sand100
)

@Composable
fun ZadTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = ZadLight,
            typography = ZadTypography,
            content = content
        )
    }
}
