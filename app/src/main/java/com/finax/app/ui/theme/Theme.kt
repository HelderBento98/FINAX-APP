package com.finax.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val IosBlue = Color(0xFF007AFF)
val IosGreen = Color(0xFF34C759)
val IosOrange = Color(0xFFFF9500)
val IosRed = Color(0xFFFF3B30)
val IosBackground = Color(0xFFF2F2F7)
val IosCardBackground = Color(0xFFFFFFFF)
val IosSecondaryText = Color(0xFF8E8E93)
val IosBorder = Color(0xFFE5E5EA)

// ---- Finax design tokens ----
val TextPrimary = Color(0xFF1C1C1E)
val SurfaceMuted = Color(0xFFF7F7F9)
val HairlineColor = Color(0xFFF0F0F2)

// Brand gradient (blue -> indigo) used on primary calls to action.
val BrandStart = Color(0xFF0A84FF)
val BrandEnd = Color(0xFF5E5CE6)

val BrandGradient: Brush
    get() = Brush.horizontalGradient(listOf(BrandStart, BrandEnd))

private val FinaxColorScheme = lightColorScheme(
    primary = IosBlue,
    secondary = IosGreen,
    tertiary = IosOrange,
    background = IosBackground,
    surface = IosCardBackground,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    error = IosRed
)

@Composable
fun FinaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FinaxColorScheme,
        content = content
    )
}
