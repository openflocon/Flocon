package io.github.openflocon.flocon.client

import android.content.Context
import android.os.Build
import android.provider.Settings
import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.model.floconMessageFromServerFromJson
import io.github.openflocon.flocon.model.toFloconMessageToServer
import io.github.openflocon.flocon.plugins.SharedPreferences.FloconSharedPreferencesPluginImpl
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPluginImpl
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPluginImpl
import io.github.openflocon.flocon.plugins.database.FloconDatabasePluginImpl
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPluginImpl
import io.github.openflocon.flocon.plugins.device.FloconDevicePluginImpl
import io.github.openflocon.flocon.plugins.files.FloconFilesPluginImpl
import io.github.openflocon.flocon.plugins.network.FloconNetworkPluginImpl
import io.github.openflocon.flocon.plugins.tables.FloconTablePluginImpl
import io.github.openflocon.flocon.utils.AppUtils
import io.github.openflocon.flocon.utils.NetUtils
import io.github.openflocon.flocon.websocket.FloconWebSocketClient
import io.github.openflocon.flocon.websocket.FloconWebSocketClientImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class FloconClientImpl(
    appContext: Context,
) : FloconApp.Client {

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
    override val devicePlugin = FloconDevicePluginImpl(sender = this)
    override val networkPlugin = FloconNetworkPluginImpl(
        context = appContext,
        sender = this,
        coroutineScope = coroutineScope,
    )

    private val allPlugins = listOf<FloconPlugin>(
        databasePlugin,
        filesPlugin,
        sharedPrefsPlugin,
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
            floconMessageFromServerFromJson(message)?.let { messageFromServer ->
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

                    Protocol.ToDevice.Network.Plugin -> {
                        networkPlugin.onMessageReceived(
                            messageFromServer = messageFromServer,
                            sender = this@FloconClientImpl,
                        )
                    }
                }
            }
        }
    }

    private val deviceId by lazy {
        Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ANDROID_ID,
        )
    }

    override fun send(
        plugin: String,
        method: String,
        body: String,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            val floconMessage = toFloconMessageToServer(
                deviceId = deviceId,
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