package io.github.openflocon.library.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import io.github.openflocon.library.designsystem.theme.FloconDarkColorScheme
import io.github.openflocon.library.designsystem.theme.FloconLightColorScheme

object FloconTheme {

    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes

}

@Composable
fun FloconTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(), // TODO Add setting and override
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme)
        FloconDarkColorScheme
    else
        FloconLightColorScheme
    val ripple = ripple()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography // Or define your custom typography,
    ) {
        CompositionLocalProvider(
            LocalIndication provides ripple,
            content = content
        )
    }
}
