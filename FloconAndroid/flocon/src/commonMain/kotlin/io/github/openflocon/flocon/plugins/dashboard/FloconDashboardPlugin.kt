package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.dashboard.mapper.toJson
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceCheckBoxValueChangedMessage
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceSubmittedFormMessage
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceSubmittedTextFieldMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FloconDashboardPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconDashboardPlugin {

    private val DashboardDispatcher = Dispatchers.Default.limitedParallelism(1)

    private val scope = CoroutineScope(DashboardDispatcher + SupervisorJob())

    private val dashboards = mutableMapOf<String, DashboardConfig>()
    private val callbackMap = mutableMapOf<String, DashboardCallback>()

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        scope.launch {
            when (messageFromServer.method) {
                Protocol.ToDevice.Dashboard.Method.OnClick -> {
                    val id = messageFromServer.body
                    callbackMap[id]?.let { it as? DashboardCallback.ButtonCallback }?.action?.invoke()
                }

                Protocol.ToDevice.Dashboard.Method.OnFormSubmitted -> {
                    ToDeviceSubmittedFormMessage.fromJson(messageFromServer.body)?.let {
                        callbackMap[it.id]?.let { it as? DashboardCallback.FormCallback }?.actions?.invoke(
                            it.values
                        )
                    }
                }

                Protocol.ToDevice.Dashboard.Method.OnTextFieldSubmitted -> {
                    ToDeviceSubmittedTextFieldMessage.fromJson(messageFromServer.body)?.let {
                        callbackMap[it.id]?.let { it as? DashboardCallback.TextFieldCallback }?.action?.invoke(
                            it.value
                        )
                    }
                }

                Protocol.ToDevice.Dashboard.Method.OnCheckBoxValueChanged -> {
                    ToDeviceCheckBoxValueChangedMessage.fromJson(messageFromServer.body)?.let {
                        callbackMap[it.id]?.let { it as? DashboardCallback.CheckBoxCallback }?.action?.invoke(
                            it.value
                        )
                    }
                }
            }
        }
    }

    override fun onConnectedToServer() {
        // on connected, send known dashboards
        dashboards.values.takeIf { it.isNotEmpty() }?.forEach { dashboardConfig ->
            registerDashboardInternal(dashboardConfig)
        }
    }

    override fun registerDashboard(dashboardConfig: DashboardConfig) {
        registerDashboardInternal(dashboardConfig)
    }

    private fun registerDashboardInternal(dashboardConfig: DashboardConfig) {
        scope.launch {
            val dashboardJson = dashboardConfig.toJson(
                registerCallback = { callback ->
                    callbackMap[callback.id] = callback
                },
            )

            dashboards.put(dashboardConfig.id, dashboardConfig)

            try {
                sender.send(
                    plugin = Protocol.FromDevice.Dashboard.Plugin,
                    method = Protocol.FromDevice.Dashboard.Method.Update,
                    body = dashboardJson.toString()
                )
            } catch (t: Throwable) {
                FloconLogger.logError("dashboard error", t)
            }
        }
    }
}

