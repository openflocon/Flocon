package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformSpecificTests(modifier: Modifier) {
    // Wasm has no platform-specific tests at the moment.
}
