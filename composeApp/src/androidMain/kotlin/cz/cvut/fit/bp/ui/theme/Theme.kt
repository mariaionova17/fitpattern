package cz.cvut.fit.bp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Color(0xFF796465),
    secondary = Color(0xFFEFE2CC),
    background = Color(0xFFF1E4CE),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF796465),
    onBackground = Color(0xFF796465),
    onSurface = Color(0xFF796465),
    outline = Color(0xFFD0C1B1)
)

@Composable
fun BpTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        content = content
    )
}