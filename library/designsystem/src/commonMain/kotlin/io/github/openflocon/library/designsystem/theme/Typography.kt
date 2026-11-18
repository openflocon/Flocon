package io.github.openflocon.library.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@Composable
fun Typography.multiplyFontSizeBy(multiplier: Float): Typography = copy(
    displayLarge = displayLarge.multiply(multiplier),
    displaySmall = displaySmall.multiply(multiplier),
    displayMedium = displayMedium.multiply(multiplier),
    headlineLarge = headlineLarge.multiply(multiplier),
    headlineMedium = headlineMedium.multiply(multiplier),
    headlineSmall = headlineSmall.multiply(multiplier),
    titleLarge = titleLarge.multiply(multiplier),
    titleMedium = titleMedium.multiply(multiplier),
    titleSmall = titleSmall.multiply(multiplier),
    bodyLarge = bodyLarge.multiply(multiplier),
    bodyMedium = bodyMedium.multiply(multiplier),
    bodySmall = bodySmall.multiply(multiplier),
    labelLarge = labelLarge.multiply(multiplier),
    labelMedium = labelMedium.multiply(multiplier),
    labelSmall = labelSmall.multiply(multiplier)
)

private fun TextStyle.multiply(multiplier: Float): TextStyle = copy(fontSize = fontSize * multiplier)
