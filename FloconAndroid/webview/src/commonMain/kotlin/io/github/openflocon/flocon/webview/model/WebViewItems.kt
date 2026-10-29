package io.github.openflocon.flocon.webview.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WebViewConsoleLog(
    @SerialName("id")
    val id: String,
    @SerialName("web_view_id")
    val webViewId: String,
    @SerialName("url")
    val url: String?,
    @SerialName("level")
    val level: String,
    @SerialName("message")
    val message: String,
    @SerialName("source_id")
    val sourceId: String?,
    @SerialName("line_number")
    val lineNumber: Int?,
    @SerialName("created_at")
    val createdAt: Long,
)

@Serializable
internal data class WebViewPageEvent(
    @SerialName("id")
    val id: String,
    @SerialName("web_view_id")
    val webViewId: String,
    @SerialName("url")
    val url: String?,
    @SerialName("type")
    val type: String,
    @SerialName("message")
    val message: String?,
    @SerialName("status_code")
    val statusCode: Int?,
    @SerialName("created_at")
    val createdAt: Long,
)

@Serializable
internal data class WebViewBridgeCall(
    @SerialName("id")
    val id: String,
    @SerialName("web_view_id")
    val webViewId: String,
    @SerialName("url")
    val url: String?,
    @SerialName("interface_name")
    val interfaceName: String,
    @SerialName("method")
    val method: String,
    @SerialName("arguments")
    val arguments: String?,
    @SerialName("result")
    val result: String?,
    @SerialName("created_at")
    val createdAt: Long,
)
