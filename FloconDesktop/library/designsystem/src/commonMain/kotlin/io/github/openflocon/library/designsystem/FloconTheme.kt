package io.github.openflocon.library.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import io.github.openflocon.library.designsystem.theme.FloconColorPalette
import io.github.openflocon.library.designsystem.theme.LocalFloconColorPalette
import io.github.openflocon.library.designsystem.theme.darkPalette
import io.github.openflocon.library.designsystem.theme.lightPalette

object FloconTheme {

    val colorPalette: FloconColorPalette
        @Composable @ReadOnlyComposable get() = LocalFloconColorPalette.current

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
    val colorPalette = when {
        isDarkTheme -> darkPalette
        else -> lightPalette
    }
    val ripple = ripple()

    MaterialTheme(typography = MaterialTheme.typography) {
        CompositionLocalProvider(
            LocalIndication provides ripple,
            LocalFloconColorPalette provides colorPalette,
            content = content
        )
    }
}
