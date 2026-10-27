package com.flocon.data.remote.bluetooth

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.bluetooth.models.BluetoothBridgeStateDomainModel
import io.github.openflocon.domain.bluetooth.models.BluetoothBridgeStateDomainModel.Status
import io.github.openflocon.domain.bluetooth.repository.BluetoothBridgeRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

internal actual fun getBluetoothBridgeRepository(): BluetoothBridgeRepository = BluetoothBridgeRepositoryJvm()

internal class BluetoothBridgeRepositoryJvm : BluetoothBridgeRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val bridge by lazy {
        BluetoothBridge(
            httpClient = HttpClient(CIO) { install(WebSockets) },
            websocketUrl = "ws://localhost:${Constant.SERVER_WEBSOCKET_PORT}/",
            uploadUrl = "http://localhost:${Constant.SERVER_HTTP_PORT}/upload",
            onError = { message, throwable -> Logger.e(throwable) { message } },
        )
    }

    private val _state = MutableStateFlow(BluetoothBridgeStateDomainModel.Idle)
    override val state: StateFlow<BluetoothBridgeStateDomainModel> = _state

    private var job: Job? = null

    // a cancelled link must not overwrite the state of the one that replaced it
    private val generation = AtomicInteger(0)

    override fun listPorts(): List<String> = try {
        SerialPortLink.listBluetoothPorts()
    } catch (t: Throwable) {
        Logger.e(t) { "Cannot list the serial ports" }
        emptyList()
    }

    @Synchronized
    override fun connect(port: String) {
        job?.cancel()
        val current = generation.incrementAndGet()
        fun update(state: BluetoothBridgeStateDomainModel) {
            if (generation.get() == current) _state.value = state
        }
        update(BluetoothBridgeStateDomainModel(Status.Connecting, port))
        job = scope.launch {
            try {
                val link = SerialPortLink.open(port)
                update(BluetoothBridgeStateDomainModel(Status.Connected, port))
                bridge.run(link)
                update(BluetoothBridgeStateDomainModel.Idle) // the phone closed the link
            } catch (c: CancellationException) {
                update(BluetoothBridgeStateDomainModel.Idle)
                throw c
            } catch (t: Throwable) {
                Logger.e(t) { "Bluetooth bridge on $port failed" }
                update(BluetoothBridgeStateDomainModel(Status.Error, port, t.message ?: t::class.simpleName))
            }
        }
    }

    @Synchronized
    override fun disconnect() {
        job?.cancel()
        job = null
    }
}
