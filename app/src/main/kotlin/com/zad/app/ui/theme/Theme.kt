package com.zad.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val ZadLight = lightColorScheme(
    primary = Night,
    onPrimary = Cream,
    primaryContainer = Cream2,
    onPrimaryContainer = Night,
    secondary = Brass,
    onSecondary = Cream,
    secondaryContainer = Paper2,
    onSecondaryContainer = BrassDeep,
    tertiary = BrassDeep,
    onTertiary = Cream,
    tertiaryContainer = Paper2,
    onTertiaryContainer = BrassDeep,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Paper2,
    onSurfaceVariant = InkSoft,
    outline = Hairline,
    outlineVariant = Cream2
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
