package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.*
import io.github.openflocon.flocon.model.*
import io.github.openflocon.flocon.plugins.analytics.FloconAnalytics
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.crashreporter.FloconCrashReporter
import io.github.openflocon.flocon.plugins.crashreporter.FloconCrashReporterPlugin
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboard
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.database.FloconDatabase
import io.github.openflocon.flocon.plugins.database.FloconDatabasePlugin
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPlugin
import io.github.openflocon.flocon.plugins.device.FloconDevice
import io.github.openflocon.flocon.plugins.device.FloconDevicePlugin
import io.github.openflocon.flocon.plugins.files.FloconFiles
import io.github.openflocon.flocon.plugins.files.FloconFilesPlugin
import io.github.openflocon.flocon.plugins.network.FloconNetwork
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferences
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferencesPlugin
import io.github.openflocon.flocon.plugins.tables.FloconTable
import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
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

internal class FloconClientImpl(
    private val appContext: FloconContext,
    private val configuration: FloconConfiguration,
) : FloconApp.Client, FloconMessageSender, FloconFileSender {

    private val FLOCON_WEBSOCKET_PORT = 9023
    private val FLOCON_HTTP_PORT = 9024

    private val appInstance by lazy {
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

    private val installedPlugins = mutableMapOf<FloconPluginKey<*, *>, Any>()
    private val pluginIdToPlugin = mutableMapOf<String, FloconPlugin>()

    init {
        configuration.pluginConfigs.forEach { (factory, config) ->
            @Suppress("UNCHECKED_CAST")
            val plugin = (factory as FloconPluginFactory<Any, Any>).install(config, FloconApp.instance!!)
            installedPlugins[factory] = plugin
            factory.pluginId?.let { id ->
                if (plugin is FloconPlugin) {
                    pluginIdToPlugin[id] = plugin
                }
            }
        }
    }

    override fun <T : Any> getPlugin(key: FloconPluginKey<*, T>): T? {
        return installedPlugins[key] as? T
    }

    // region plugins backward compatibility
    override val databasePlugin: FloconDatabasePlugin? get() = getPlugin(FloconDatabase)
    override val dashboardPlugin: FloconDashboardPlugin? get() = getPlugin(FloconDashboard)
    override val tablePlugin: FloconTablePlugin? get() = getPlugin(FloconTable)
    override val deeplinksPlugin: FloconDeeplinksPlugin? get() = getPlugin(FloconDeeplinks)
    override val analyticsPlugin: FloconAnalyticsPlugin? get() = getPlugin(FloconAnalytics)
    override val networkPlugin: FloconNetworkPlugin? get() = getPlugin(FloconNetwork)
    override val devicePlugin: FloconDevicePlugin? get() = getPlugin(FloconDevice)
    override val preferencesPlugin: FloconPreferencesPlugin? get() = getPlugin(FloconPreferences)
    override val crashReporterPlugin: FloconCrashReporterPlugin? get() = getPlugin(FloconCrashReporter)
    // endregion

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
        installedPlugins.values.forEach {
            if (it is FloconPlugin) {
                it.onConnectedToServer()
            }
        }
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    private fun onMessageReceived(message: String) {
        coroutineScope.launch(Dispatchers.IO) {
            floconMessageFromServerFromJson(message)?.let { messageFromServer ->
                pluginIdToPlugin[messageFromServer.plugin]?.onMessageReceived(
                    method = messageFromServer.method,
                    body = messageFromServer.body,
                )
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