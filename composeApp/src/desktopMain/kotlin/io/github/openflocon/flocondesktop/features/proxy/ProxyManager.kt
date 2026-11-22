package io.github.openflocon.flocondesktop.features.proxy

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.net.ServerSocket
import java.util.concurrent.TimeUnit

/**
 * Starts and watches the `flocon-proxy` binary shipped in the app native resources
 * (built from `proxy/`, see `appResourcesRootDir` in composeApp/build.gradle.kts).
 *
 * The proxy connects back to the Flocon websocket like any SDK, so the calls it sees
 * land in the Network screen, one device per phone using the proxy.
 */
class ProxyManager(
    private val floconWebsocketUrl: String,
    private val dataDir: File,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow<ProxyState>(ProxyState.Stopped)
    val state: StateFlow<ProxyState> = _state.asStateFlow()

    @Volatile private var process: Process? = null
    private var job: Job? = null

    @Volatile private var wantedRunning = false

    init {
        Runtime.getRuntime().addShutdownHook(Thread { killProcess() })
    }

    fun start(preferredPort: Int = DEFAULT_PROXY_PORT) {
        if (wantedRunning) return
        wantedRunning = true
        job = scope.launch(Dispatchers.IO) { runWithRestart(preferredPort) }
    }

    fun stop() {
        wantedRunning = false
        job?.cancel()
        job = null
        killProcess()
        _state.value = ProxyState.Stopped
    }

    private suspend fun runWithRestart(preferredPort: Int) {
        var backoffMs = MIN_BACKOFF_MS
        while (wantedRunning && currentCoroutineContext().isActive) {
            val startedAt = System.currentTimeMillis()
            val exitCode = runOnce(preferredPort) ?: return
            if (!wantedRunning) return
            // The process ran for a while: it is a one-off crash, not a crash loop.
            if (System.currentTimeMillis() - startedAt > STABLE_RUN_MS) backoffMs = MIN_BACKOFF_MS
            if (_state.value !is ProxyState.Failed) {
                _state.value = ProxyState.Failed("Proxy stopped (exit code $exitCode), restarting…")
            }
            delay(backoffMs)
            backoffMs = (backoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
        }
    }

    /** @return the exit code, or null when the proxy cannot be started at all. */
    private fun runOnce(preferredPort: Int): Int? {
        _state.value = ProxyState.Starting
        val binary = resolveBinary()
        if (binary == null) {
            _state.value = ProxyState.Failed("flocon-proxy binary not found for this platform")
            wantedRunning = false
            return null
        }
        dataDir.mkdirs()
        val proxyPort = freePort(preferredPort)
        val caPort = freePort(proxyPort + 1)

        val p = try {
            ProcessBuilder(
                binary.absolutePath,
                "--port", proxyPort.toString(),
                "--ca-port", caPort.toString(),
                "--flocon-ws", floconWebsocketUrl,
                "--ca-dir", File(dataDir, "proxy-ca").absolutePath,
            )
                .redirectError(ProcessBuilder.Redirect.appendTo(File(dataDir, "flocon-proxy.log")))
                .start()
        } catch (t: Throwable) {
            Logger.e(t) { "Cannot start flocon-proxy" }
            _state.value = ProxyState.Failed(t.message ?: "Cannot start flocon-proxy")
            wantedRunning = false
            return null
        }
        process = p

        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                when (val event = parseProxyEvent(line)) {
                    is ProxyEvent.Ready -> _state.value = ProxyState.Running(event.proxyPort, event.caPort, event.ips)
                    is ProxyEvent.Error -> _state.value = ProxyState.Failed(event.message)
                    is ProxyEvent.TlsError -> Logger.w { "flocon-proxy TLS error on ${event.host}: ${event.reason}" }
                    null -> Unit
                }
            }
        }
        return p.waitFor()
    }

    private fun killProcess() {
        val p = process ?: return
        process = null
        p.destroy() // SIGTERM: lets the proxy close its connections
        if (!p.waitFor(2, TimeUnit.SECONDS)) p.destroyForcibly()
    }

    private fun resolveBinary(): File? {
        val name = if (currentPlatformFolder().startsWith("windows")) "flocon-proxy.exe" else "flocon-proxy"
        // Packaged app: Compose Desktop exposes the native resources folder of the current OS.
        val packaged = System.getProperty("compose.application.resources.dir")?.let { File(it, name) }
        // `./gradlew run`: read from the source tree.
        val dev = File("appResources/${currentPlatformFolder()}/$name")
        return listOfNotNull(packaged, dev)
            .firstOrNull { it.exists() }
            ?.also { it.setExecutable(true) }
    }

    private fun freePort(preferred: Int): Int = runCatching { ServerSocket(preferred).use { it.localPort } }
        .getOrElse { ServerSocket(0).use { it.localPort } }

    companion object {
        const val DEFAULT_PROXY_PORT = 8080
        private const val MIN_BACKOFF_MS = 1_000L
        private const val MAX_BACKOFF_MS = 30_000L
        private const val STABLE_RUN_MS = 30_000L
    }
}

internal fun currentPlatformFolder(
    osName: String = System.getProperty("os.name"),
    osArch: String = System.getProperty("os.arch"),
): String {
    val os = osName.lowercase()
    val arm = osArch.lowercase().let { it == "aarch64" || it == "arm64" }
    return when {
        "mac" in os -> if (arm) "macos-arm64" else "macos-x64"
        "win" in os -> "windows-x64"
        else -> "linux-x64"
    }
}
