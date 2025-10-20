package io.github.openflocon.flocon.plugins.device

import com.jakewharton.processphoenix.ProcessPhoenix
import io.github.openflocon.flocon.FloconContext

internal actual fun restartApp(context: FloconContext) {
    ProcessPhoenix.triggerRebirth(context.appContext)
}