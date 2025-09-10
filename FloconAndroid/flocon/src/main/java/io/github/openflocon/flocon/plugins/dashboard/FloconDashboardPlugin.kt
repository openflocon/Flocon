package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.dashboard.mapper.toJson
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardCallback
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceCheckBoxValueChangedMessage
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceSubmittedFormMessage
import io.github.openflocon.flocon.plugins.dashboard.model.todevice.ToDeviceSubmittedTextFieldMessage
import java.util.concurrent.ConcurrentHashMap

class FloconDashboardPluginImpl(
    private val sender: FloconMessageSender,
) : FloconDashboardPlugin {

    private val dashboards = ConcurrentHashMap<String, DashboardConfig>()
    private val callbackMap: MutableMap<String, DashboardCallback> = ConcurrentHashMap()

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
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

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // on connected, send known dashboards
        dashboards.values.takeIf { it.isNotEmpty() }?.forEach { dashboardConfig ->
            registerDashboard(dashboardConfig)
        }
    }

    override fun registerDashboard(dashboardConfig: DashboardConfig) {
        val dashboardJson = dashboardConfig.toJson(
            registerCallback = { callback ->
                callbackMap[callback.id] = callback
            },
        )

        dashboards.put(dashboardConfig.id, dashboardConfig)

        sender.send(
            plugin = Protocol.FromDevice.Dashboard.Plugin,
            method = Protocol.FromDevice.Dashboard.Method.Update,
            body = dashboardJson.toString()
        )
    }
}

