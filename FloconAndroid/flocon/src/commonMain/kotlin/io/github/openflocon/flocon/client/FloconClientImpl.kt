package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.core.getAppInfos
import io.github.openflocon.flocon.getServerHost
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.model.FloconMessageToServer
import io.github.openflocon.flocon.model.floconMessageFromServerFromJson
import io.github.openflocon.flocon.model.toFloconMessageToServer
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPluginImpl
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPluginImpl
import io.github.openflocon.flocon.plugins.database.FloconDatabasePluginImpl
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPluginImpl
import io.github.openflocon.flocon.plugins.device.FloconDevicePluginImpl
import io.github.openflocon.flocon.plugins.files.FloconFilesPluginImpl
import io.github.openflocon.flocon.plugins.network.FloconNetworkPluginImpl
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferencesPluginImpl
import io.github.openflocon.flocon.plugins.tables.FloconTablePluginImpl
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.websocket.FloconHttpClient
import io.github.openflocon.flocon.websocket.FloconWebSocketClient
import io.github.openflocon.flocon.websocket.buildFloconHttpClient
import io.github.openflocon.flocon.websocket.buildFloconWebSocketClient
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.getValue

internal class FloconClientImpl(
    private val appContext: FloconContext,
) : FloconApp.Client, FloconMessageSender, FloconFileSender {

    private val FLOCON_WEBSOCKET_PORT = 9023
    private val FLOCON_HTTP_PORT = 9024

    private val appInstance by lazy {
        // store the start time of the sdk, for this app launch
        currentTimeMillis()
    }

    private val appInfos by lazy {
        getAppInfos(appContext)
    }

    private val versionName by lazy {
        BuildConfig.APP_VERSION
    }

    private val webSocketClient: FloconWebSocketClient = buildFloconWebSocketClient()
    private val httpClient: FloconHttpClient = buildFloconHttpClient()

    private val address by lazy {
        getServerHost(appContext)
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // region plugins
    override val databasePlugin = FloconDatabasePluginImpl(context = appContext, sender = this)
    private val filesPlugin = FloconFilesPluginImpl(context = appContext, sender = this, floconFileSender = this)
    override val preferencesPlugin = FloconPreferencesPluginImpl(context = appContext, sender = this, scope = coroutineScope)
    override val dashboardPlugin = FloconDashboardPluginImpl(sender = this)
    override val tablePlugin = FloconTablePluginImpl(sender = this)
    override val deeplinksPlugin = FloconDeeplinksPluginImpl(sender = this)
    override val analyticsPlugin = FloconAnalyticsPluginImpl(sender = this)
    override val devicePlugin = FloconDevicePluginImpl(sender = this, context = appContext)
    override val networkPlugin = FloconNetworkPluginImpl(
        context = appContext,
        sender = this,
        coroutineScope = coroutineScope,
    )

    private val allPlugins = listOf<FloconPlugin>(
        databasePlugin,
        filesPlugin,
        preferencesPlugin,
        dashboardPlugin,
        tablePlugin,
        deeplinksPlugin,
        analyticsPlugin,
        networkPlugin,
        devicePlugin,
    )

    @Throws(Throwable::class)
    override suspend fun connect(
        onClosed: () -> Unit,
    ) {
        webSocketClient.connect(
            address = address,
            port = FLOCON_WEBSOCKET_PORT,
            onMessageReceived = ::onMessageReceived,
            onClosed = onClosed,
        )
        allPlugins.forEach {
            it.onConnectedToServer()
        }
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    private fun onMessageReceived(message: String) {
        coroutineScope.launch(Dispatchers.IO) {
            floconMessageFromServerFromJson(message)?.let { messageFromServer ->
                when (messageFromServer.plugin) {
                    Protocol.ToDevice.Database.Plugin -> {
                        databasePlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Files.Plugin -> {
                        filesPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.SharedPreferences.Plugin -> {
                        preferencesPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Device.Plugin -> {
                        devicePlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Dashboard.Plugin -> {
                        dashboardPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Table.Plugin -> {
                        tablePlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Analytics.Plugin -> {
                        analyticsPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                        )
                    }

                    Protocol.ToDevice.Network.Plugin -> {
                        networkPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
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
            webSocketClient.sendMessage(
                message = FloconMessageToServer(
                    deviceId = appInfos.deviceId,
                    plugin = plugin,
                    body = body,
                    appName = appInfos.appName,
                    appPackageName = appInfos.appPackageName,
                    method = method,
                    deviceName = appInfos.deviceName,
                    appInstance = appInstance,
                    platform = appInfos.platform,
                    versionName = versionName,
                ).toFloconMessageToServer(),
            )
        }
    }

    override fun send(
        file: FloconFile,
        infos: FloconFileInfo,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            httpClient.send(
                address = address,
                port = FLOCON_HTTP_PORT,
                file = file,
                infos = infos,

                deviceId = appInfos.deviceId,
                appPackageName = appInfos.appPackageName,
                appInstance = appInstance,
            )
        }
    }

    override fun sendPendingMessages() {
        coroutineScope.launch(Dispatchers.IO) {
            webSocketClient.sendPendingMessages()
        }
    }
}