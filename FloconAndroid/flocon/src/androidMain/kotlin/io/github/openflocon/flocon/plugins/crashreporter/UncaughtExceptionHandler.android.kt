package io.github.openflocon.flocon.plugins.crashreporter

import android.os.Build
import io.github.openflocon.flocon.FloconContext

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        try {
            // Save crash
            onCrash(throwable)
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            // Call original handler to let the app crash normally
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
