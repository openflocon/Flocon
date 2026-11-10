package com.flocon.data.remote.webview.mapper

import com.flocon.data.remote.webview.model.WebViewBridgeCallDataModel
import com.flocon.data.remote.webview.model.WebViewConsoleLogDataModel
import com.flocon.data.remote.webview.model.WebViewPageEventDataModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind

internal fun WebViewConsoleLogDataModel.toDomain() = WebViewEventDomainModel(
    id = id,
    webViewId = webViewId,
    kind = WebViewEventKind.Console,
    url = url,
    label = level,
    message = message,
    sourceId = sourceId,
    lineNumber = lineNumber,
    statusCode = null,
    result = null,
    timestamp = createdAt,
)

internal fun WebViewPageEventDataModel.toDomain() = WebViewEventDomainModel(
    id = id,
    webViewId = webViewId,
    kind = WebViewEventKind.Page,
    url = url,
    label = type,
    message = message,
    sourceId = null,
    lineNumber = null,
    statusCode = statusCode,
    result = null,
    timestamp = createdAt,
)

internal fun WebViewBridgeCallDataModel.toDomain() = WebViewEventDomainModel(
    id = id,
    webViewId = webViewId,
    kind = WebViewEventKind.Bridge,
    url = url,
    label = "$interfaceName.$method",
    message = arguments,
    sourceId = null,
    lineNumber = null,
    statusCode = null,
    result = result,
    timestamp = createdAt,
)
