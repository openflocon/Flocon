package io.github.openflocon.flocon.webview

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.webview.model.WebViewBridgeCall
import io.github.openflocon.flocon.webview.model.WebViewConsoleLog
import io.github.openflocon.flocon.webview.model.WebViewPageEvent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FloconWebViewConfig : FloconPluginConfig

enum class WebViewConsoleLevel(internal val wireName: String) {
    Log("log"),
    Debug("debug"),
    Info("info"),
    Warn("warn"),
    Error("error"),
}

enum class WebViewPageEventType(internal val wireName: String) {
    PageStarted("page_started"),
    PageFinished("page_finished"),
    Navigation("navigation"),
    Error("error"),
    HttpError("http_error"),
    SslError("ssl_error"),
}

/**
 * Sends what happens in a WebView to Flocon.
 * On Android and iOS, prefer the `attach` helpers, which call these functions for you.
 */
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
    override val pluginId: String = Protocol.ToDevice.WebView.Plugin
    override fun createConfig(context: FloconContext) = FloconWebViewConfig()
    override fun install(
        pluginConfig: FloconWebViewConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder,
    ): FloconWebViewPlugin {
        return FloconWebViewPluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder,
        )
            .also { FloconWebViewPluginImpl.plugin = it }
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
internal class FloconWebViewPluginImpl(
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder,
) : FloconPlugin, FloconWebViewPlugin {
    override val key: String
        get() = Protocol.ToDevice.WebView.Plugin

    override suspend fun onMessageReceived(method: String, body: String) {
        // no op
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    override fun logConsole(
        webViewId: String,
        level: WebViewConsoleLevel,
        message: String,
        url: String?,
        sourceId: String?,
        lineNumber: Int?,
    ) {
        send(
            method = Protocol.FromDevice.WebView.Method.AddConsoleLogs,
            body = {
                encoder.encode<List<WebViewConsoleLog>>(
                    listOf(
                        WebViewConsoleLog(
                            id = newId(),
                            webViewId = webViewId,
                            url = url,
                            level = level.wireName,
                            message = message,
                            sourceId = sourceId,
                            lineNumber = lineNumber,
                            createdAt = now(),
                        ),
                    ),
                )
            },
        )
    }

    override fun logPageEvent(
        webViewId: String,
        type: WebViewPageEventType,
        url: String?,
        message: String?,
        statusCode: Int?,
    ) {
        send(
            method = Protocol.FromDevice.WebView.Method.AddPageEvents,
            body = {
                encoder.encode<List<WebViewPageEvent>>(
                    listOf(
                        WebViewPageEvent(
                            id = newId(),
                            webViewId = webViewId,
                            url = url,
                            type = type.wireName,
                            message = message,
                            statusCode = statusCode,
                            createdAt = now(),
                        ),
                    ),
                )
            },
        )
    }

    override fun logBridgeCall(
        webViewId: String,
        interfaceName: String,
        method: String,
        arguments: String?,
        result: String?,
        url: String?,
    ) {
        send(
            method = Protocol.FromDevice.WebView.Method.AddBridgeCalls,
            body = {
                encoder.encode<List<WebViewBridgeCall>>(
                    listOf(
                        WebViewBridgeCall(
                            id = newId(),
                            webViewId = webViewId,
                            url = url,
                            interfaceName = interfaceName,
                            method = method,
                            arguments = arguments,
                            result = result,
                            createdAt = now(),
                        ),
                    ),
                )
            },
        )
    }

    private fun send(method: String, body: () -> String) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.WebView.Plugin,
                method = method,
                body = body(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("error on WebView $method", t)
        }
    }

    private fun newId() = Uuid.random().toString()

    private fun now() = Clock.System.now().toEpochMilliseconds()

    companion object {
        var plugin: FloconWebViewPlugin? = null
    }
}

val Flocon.Companion.webViewPlugin: FloconWebViewPlugin
    get() = FloconWebViewPluginImpl.plugin ?: pluginNotInitialized("WebView", NoOpWebViewPlugin())

private class NoOpWebViewPlugin : FloconWebViewPlugin {
    override val key: String = ""
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
