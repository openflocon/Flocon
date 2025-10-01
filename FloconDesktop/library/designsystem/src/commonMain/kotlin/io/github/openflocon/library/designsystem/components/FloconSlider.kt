package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        steps = steps,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            activeTrackColor = FloconTheme.colorPalette.secondary,
            thumbColor = FloconTheme.colorPalette.onSecondary
        ),
        modifier = modifier
    )
}
