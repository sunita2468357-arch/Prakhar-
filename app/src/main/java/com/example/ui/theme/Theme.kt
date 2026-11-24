package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class ThemeMode(val label: String) {
    DARK("Dark"),
    LIGHT("Light"),
    SYSTEM("System")
}

private val NaterDarkColorScheme = darkColorScheme(
    primary = NaterTealDark,
    onPrimary = ObsidianBackground,
    primaryContainer = NaterTealContainerDark,
    onPrimaryContainer = NaterTealDark,
    secondary = NaterSkyDark,
    onSecondary = ObsidianBackground,
    secondaryContainer = ObsidianSurfaceElevated,
    onSecondaryContainer = NaterTextPrimaryDark,
    tertiary = NaterAmberDark,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = NaterTextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = NaterTextPrimaryDark,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = NaterTextSecondaryDark,
    surfaceContainerLowest = ObsidianBackground,
    surfaceContainerLow = ObsidianSurface,
    surfaceContainer = ObsidianSurfaceElevated,
    surfaceContainerHigh = ObsidianSurfaceHighest,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder,
    error = NaterErrorDark,
    onError = ObsidianBackground
)

private val NaterLightColorScheme = lightColorScheme(
    primary = NaterTealLight,
    onPrimary = CrispSurface,
    primaryContainer = NaterTealContainerLight,
    onPrimaryContainer = NaterTealLight,
    secondary = NaterSkyLight,
    onSecondary = CrispSurface,
    secondaryContainer = CrispSurfaceElevated,
    onSecondaryContainer = NaterTextPrimaryLight,
    tertiary = NaterAmberLight,
    onTertiary = CrispSurface,
    background = CrispBackground,
    onBackground = NaterTextPrimaryLight,
    surface = CrispSurface,
    onSurface = NaterTextPrimaryLight,
    surfaceVariant = CrispSurfaceElevated,
    onSurfaceVariant = NaterTextSecondaryLight,
    surfaceContainerLowest = CrispSurface,
    surfaceContainerLow = CrispBackground,
    surfaceContainer = CrispSurfaceElevated,
    surfaceContainerHigh = CrispSurfaceElevated,
    outline = CrispBorder,
    outlineVariant = CrispBorder,
    error = NaterErrorLight,
    onError = CrispSurface
)

val NaterShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NaterTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    fontScaleLevel: FontScaleLevel = FontScaleLevel.STANDARD,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (useDark) NaterDarkColorScheme else NaterLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = buildNaterTypography(fontScaleLevel),
        shapes = NaterShapes,
        content = content
    )
}
