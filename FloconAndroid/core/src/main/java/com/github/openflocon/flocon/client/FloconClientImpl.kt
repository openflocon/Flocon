package com.github.openflocon.flocon.client

import android.content.Context
import android.os.Build
import com.github.openflocon.flocon.Flocon
import com.github.openflocon.flocon.Protocol
import com.github.openflocon.flocon.core.FloconPlugin
import com.github.openflocon.flocon.model.FloconMessageFromServer
import com.github.openflocon.flocon.model.toFloconMessageToServer
import com.github.openflocon.flocon.plugins.SharedPreferences.FloconSharedPreferencesPluginImpl
import com.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPluginImpl
import com.github.openflocon.flocon.plugins.dashboard.FloconDashboardPluginImpl
import com.github.openflocon.flocon.plugins.database.FloconDatabasePluginImpl
import com.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPluginImpl
import com.github.openflocon.flocon.plugins.files.FloconFilesPluginImpl
import com.github.openflocon.flocon.plugins.tables.FloconTablePluginImpl
import com.github.openflocon.flocon.utils.AppUtils
import com.github.openflocon.flocon.utils.NetUtils
import com.github.openflocon.flocon.websocket.FloconWebSocketClient
import com.github.openflocon.flocon.websocket.FloconWebSocketClientImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FloconClientImpl(
    appContext: Context,
) : Flocon.Client {

    private val FLOCON_PORT = 9023

    private val webSocketClient: FloconWebSocketClient = FloconWebSocketClientImpl()

    private val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
    private val appName = AppUtils.getAppName(appContext)
    private val appPackageName = AppUtils.getAppPackageName(appContext)

    private val address = NetUtils.getServerHost(context = appContext)
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // region plugins
    private val databasePlugin = FloconDatabasePluginImpl(context = appContext)
    private val filesPlugin = FloconFilesPluginImpl(context = appContext)
    private val sharedPrefsPlugin = FloconSharedPreferencesPluginImpl(context = appContext)
    override val dashboardPlugin = FloconDashboardPluginImpl(sender = this)
    override val tablePlugin = FloconTablePluginImpl(sender = this)
    override val deeplinksPlugin = FloconDeeplinksPluginImpl(sender = this)
    override val analyticsPlugin = FloconAnalyticsPluginImpl(sender = this)

    private val allPlugins = listOf<FloconPlugin>(
        databasePlugin,
        filesPlugin,
        sharedPrefsPlugin,
        dashboardPlugin,
        tablePlugin,
        deeplinksPlugin,
        analyticsPlugin,
    )

    @Throws(Throwable::class)
    override suspend fun connect(
        onClosed: () -> Unit,
    ) {
        webSocketClient.connect(
            address = address,
            port = FLOCON_PORT,
            onMessageReceived = ::onMessageReceived,
            onClosed = onClosed,
        )
        allPlugins.forEach {
            it.onConnectedToServer(sender = this)
        }
    }

    override fun disconnect() {
        webSocketClient.disconnect()
    }

    private fun onMessageReceived(message: String) {
        coroutineScope.launch(Dispatchers.IO) {
            FloconMessageFromServer.fromJson(message)?.let { messageFromServer ->
                when (messageFromServer.plugin) {
                    Protocol.ToDevice.Database.Plugin -> {
                        databasePlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }

                    Protocol.ToDevice.Files.Plugin -> {
                        filesPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }

                    Protocol.ToDevice.SharedPreferences.Plugin -> {
                        sharedPrefsPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }

                    Protocol.ToDevice.Dashboard.Plugin -> {
                        dashboardPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }

                    Protocol.ToDevice.Table.Plugin -> {
                        tablePlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }

                    Protocol.ToDevice.Analytics.Plugin -> {
                        analyticsPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }
                }
            }
        }
    }

    override fun send(
        plugin: String,
        method: String,
        body: String,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            val floconMessage = toFloconMessageToServer(
                plugin = plugin,
                body = body,
                appName = appName,
                appPackageName = appPackageName,
                method = method,
                deviceName = deviceName,
            )
            webSocketClient.sendMessage(
                message = floconMessage,
            )
        }
    }
}