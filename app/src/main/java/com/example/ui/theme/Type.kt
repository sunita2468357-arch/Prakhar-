package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Medium)
)

enum class FontScaleLevel(val multiplier: Float, val label: String) {
    STANDARD(1.0f, "Standard"),
    COMFORTABLE(1.12f, "Comfortable"),
    LARGE(1.26f, "Large Accessible")
}

fun buildNaterTypography(scale: FontScaleLevel = FontScaleLevel.STANDARD): Typography {
    val m = scale.multiplier
    return Typography(
        displaySmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (28 * m).sp,
            lineHeight = (36 * m).sp,
            letterSpacing = (-0.4).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * m).sp,
            lineHeight = (30 * m).sp,
            letterSpacing = (-0.2).sp
        ),
        titleLarge = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (19 * m).sp,
            lineHeight = (26 * m).sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 * m).sp,
            lineHeight = (23 * m).sp
        ),
        titleSmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * m).sp,
            lineHeight = (20 * m).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (15.5f * m).sp,
            lineHeight = (24 * m).sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * m).sp,
            lineHeight = (21 * m).sp,
            letterSpacing = 0.15.sp
        ),
        bodySmall = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (12.5f * m).sp,
            lineHeight = (18 * m).sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (14 * m).sp,
            lineHeight = (20 * m).sp
        ),
        labelMedium = TextStyle(
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (11.5f * m).sp,
            lineHeight = (16 * m).sp
        ),
        labelSmall = TextStyle(
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (10.5f * m).sp,
            lineHeight = (14 * m).sp
        )
    )
}
