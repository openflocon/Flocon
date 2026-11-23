package io.github.openflocon.flocondesktop.features.proxy

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val dataDir: File,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow<ProxyStateDomainModel>(ProxyStateDomainModel.Stopped)
    val state: StateFlow<ProxyStateDomainModel> = _state.asStateFlow()

    @Volatile private var process: Process? = null
    private var job: Job? = null

    // start / stop never overlap, so that only one process runs at a time
    private val mutex = Mutex()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { killProcess() })
    }

    /** Starts the proxy, or restarts it when it already runs (e.g. the websocket port changed). */
    suspend fun start(floconWebsocketUrl: String, preferredPort: Int = DEFAULT_PROXY_PORT) = mutex.withLock {
        stopLocked()
        job = scope.launch(Dispatchers.IO) { runWithRestart(floconWebsocketUrl, preferredPort) }
    }

    suspend fun stop() = mutex.withLock { stopLocked() }

    private suspend fun stopLocked() {
        job?.let {
            it.cancel()
            // the stdout reader of the process ends once the process is dead
            killProcess()
            it.join()
        }
        job = null
        _state.value = ProxyStateDomainModel.Stopped
    }

    private suspend fun runWithRestart(floconWebsocketUrl: String, preferredPort: Int) {
        var backoffMs = MIN_BACKOFF_MS
        while (currentCoroutineContext().isActive) {
            val startedAt = System.currentTimeMillis()
            val exitCode = runOnce(floconWebsocketUrl, preferredPort) ?: return
            if (!currentCoroutineContext().isActive) return
            // The process ran for a while: it is a one-off crash, not a crash loop.
            if (System.currentTimeMillis() - startedAt > STABLE_RUN_MS) backoffMs = MIN_BACKOFF_MS
            if (_state.value !is ProxyStateDomainModel.Failed) {
                _state.value = ProxyStateDomainModel.Failed("Proxy stopped (exit code $exitCode), restarting…")
            }
            delay(backoffMs)
            backoffMs = (backoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
        }
    }

    /** @return the exit code, or null when the proxy cannot be started at all (or was stopped meanwhile). */
    private suspend fun runOnce(floconWebsocketUrl: String, preferredPort: Int): Int? {
        _state.value = ProxyStateDomainModel.Starting
        val binary = resolveBinary()
        if (binary == null) {
            _state.value = ProxyStateDomainModel.BinaryNotFound
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
            _state.value = ProxyStateDomainModel.Failed(t.message ?: "Cannot start flocon-proxy")
            return null
        }
        process = p
        // stopped while starting: stop() may have run before `process` was set
        if (!currentCoroutineContext().isActive) {
            killProcess()
            return null
        }

        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                when (val event = parseProxyEvent(line)) {
                    is ProxyEvent.Ready -> _state.value = ProxyStateDomainModel.Running(event.proxyPort, event.caPort, event.ips)
                    is ProxyEvent.Error -> _state.value = ProxyStateDomainModel.Failed(event.message)
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
        else -> if (arm) "linux-arm64" else "linux-x64"
    }
}
