package cz.cvut.fit.bp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cz.cvut.fit.bp.R

val CaprasimoFontFamily = FontFamily(
    Font(R.font.caprasimo_regular, FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CaprasimoFontFamily,
        fontSize = 11.sp
    )
)