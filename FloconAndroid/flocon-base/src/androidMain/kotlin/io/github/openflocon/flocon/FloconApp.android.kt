package io.github.openflocon.flocon

import android.content.Context

actual fun FloconApp.initializePlatform(platformContext: Any?) {
    // On Android, platformContext should be a Context
    // You can use it for Android-specific initialization if needed
    if (platformContext is Context) {
        // Android-specific initialization with context
    }
}

