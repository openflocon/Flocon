package com.flocon.data.remote.webview.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WebViewConsoleLogDataModel(
    @SerialName("id") val id: String,
    @SerialName("web_view_id") val webViewId: String,
    @SerialName("url") val url: String? = null,
    @SerialName("level") val level: String,
    @SerialName("message") val message: String,
    @SerialName("source_id") val sourceId: String? = null,
    @SerialName("line_number") val lineNumber: Int? = null,
    @SerialName("created_at") val createdAt: Long,
)

@Serializable
internal data class WebViewPageEventDataModel(
    @SerialName("id") val id: String,
    @SerialName("web_view_id") val webViewId: String,
    @SerialName("url") val url: String? = null,
    @SerialName("type") val type: String,
    @SerialName("message") val message: String? = null,
    @SerialName("status_code") val statusCode: Int? = null,
    @SerialName("created_at") val createdAt: Long,
)

@Serializable
internal data class WebViewBridgeCallDataModel(
    @SerialName("id") val id: String,
    @SerialName("web_view_id") val webViewId: String,
    @SerialName("url") val url: String? = null,
    @SerialName("interface_name") val interfaceName: String,
    @SerialName("method") val method: String,
    @SerialName("arguments") val arguments: String? = null,
    @SerialName("result") val result: String? = null,
    @SerialName("created_at") val createdAt: Long,
)
