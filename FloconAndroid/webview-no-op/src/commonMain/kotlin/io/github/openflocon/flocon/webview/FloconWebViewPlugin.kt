package io.github.openflocon.flocon.webview

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.core.FloconEncoder

class FloconWebViewConfig : FloconPluginConfig

enum class WebViewConsoleLevel {
    Log,
    Debug,
    Info,
    Warn,
    Error,
}

enum class WebViewPageEventType {
    PageStarted,
    PageFinished,
    Navigation,
    Error,
    HttpError,
    SslError,
}

interface FloconWebViewPlugin : FloconPlugin {
    fun logConsole(
        webViewId: String,
        level: WebViewConsoleLevel,
        message: String,
        url: String? = null,
        sourceId: String? = null,
        lineNumber: Int? = null,
    )

    fun logPageEvent(
        webViewId: String,
        type: WebViewPageEventType,
        url: String? = null,
        message: String? = null,
        statusCode: Int? = null,
    )

    fun logBridgeCall(
        webViewId: String,
        interfaceName: String,
        method: String,
        arguments: String? = null,
        result: String? = null,
        url: String? = null,
    )
}

object FloconWebView : FloconPluginFactory<FloconWebViewConfig, FloconWebViewPlugin> {
    override val name: String = "WebView"
    override val pluginId: String = "webview"
    override fun createConfig(context: FloconContext) = FloconWebViewConfig()
    override fun install(
        pluginConfig: FloconWebViewConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder,
    ): FloconWebViewPlugin = FloconWebViewPluginNoOp
}

internal object FloconWebViewPluginNoOp : FloconWebViewPlugin {
    override val key: String = "webview"

    override fun logConsole(
        webViewId: String,
        level: WebViewConsoleLevel,
        message: String,
        url: String?,
        sourceId: String?,
        lineNumber: Int?,
    ) = Unit

    override fun logPageEvent(
        webViewId: String,
        type: WebViewPageEventType,
        url: String?,
        message: String?,
        statusCode: Int?,
    ) = Unit

    override fun logBridgeCall(
        webViewId: String,
        interfaceName: String,
        method: String,
        arguments: String?,
        result: String?,
        url: String?,
    ) = Unit

    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}

val Flocon.Companion.webViewPlugin: FloconWebViewPlugin
    get() = FloconWebViewPluginNoOp
