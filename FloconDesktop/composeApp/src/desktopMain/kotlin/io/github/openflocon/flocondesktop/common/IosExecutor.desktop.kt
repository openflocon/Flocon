package io.github.openflocon.flocondesktop.common

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.settings.usecase.IosExecutor
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

// Simple executor for iOS forwarding using iproxy
// Requires libimobiledevice to be installed (brew install libimobiledevice)
class IosExecutorDesktop : IosExecutor {

    private val processes = ConcurrentHashMap<Int, Process>()

    override fun startForward(localPort: Int, devicePort: Int, onOutput: (String) -> Unit) {
        if (processes.containsKey(localPort)) {
            // Already running
            return
        }

        Logger.d { "Starting iOS forwarding: $localPort -> $devicePort" }

        // Try to find iproxy in common paths since it's not always in PATH for UI apps
        val iproxyPath = findIproxyPath() ?: "iproxy"

        try {
            val process = ProcessBuilder(iproxyPath, localPort.toString(), devicePort.toString())
                .redirectErrorStream(true)
                .start()

            processes[localPort] = process

            // Consume output in a separate thread to avoid blocking and buffer filling
            Thread {
                try {
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            Logger.d { line }
                            onOutput(line)
                        }
                    }
                } catch (e: Exception) {
                    // unexpected termination
                } finally {
                    processes.remove(localPort)
                    // If process died, we might want to restart? logic handles loop in AppViewModel
                }
            }.start()

        } catch (e: IOException) {
            Logger.e(e) { "Failed to start iproxy: ${e.message}" }
            onOutput("Failed to start iproxy: ${e.message}")
        }
    }

    override fun stopForward(localPort: Int) {
        processes.remove(localPort)?.destroy()
    }

    private fun findIproxyPath(): String? {
        val paths = listOf(
            "/opt/homebrew/bin/iproxy",
            "/usr/local/bin/iproxy",
            "/usr/bin/iproxy"
        )
        return paths.firstOrNull { java.io.File(it).exists() }
    }
}
