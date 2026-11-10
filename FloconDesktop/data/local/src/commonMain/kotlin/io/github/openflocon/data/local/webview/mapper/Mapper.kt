package io.github.openflocon.data.local.webview.mapper

import io.github.openflocon.data.local.webview.models.WebViewEventEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind

internal fun WebViewEventDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
) = WebViewEventEntity(
    eventId = id,
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    webViewId = webViewId,
    kind = kind.name,
    url = url,
    label = label,
    message = message,
    sourceId = sourceId,
    lineNumber = lineNumber,
    statusCode = statusCode,
    result = result,
    timestamp = timestamp,
)

internal fun WebViewEventEntity.toDomain() = WebViewEventDomainModel(
    id = eventId,
    webViewId = webViewId,
    kind = WebViewEventKind.entries.firstOrNull { it.name == kind } ?: WebViewEventKind.Console,
    url = url,
    label = label,
    message = message,
    sourceId = sourceId,
    lineNumber = lineNumber,
    statusCode = statusCode,
    result = result,
    timestamp = timestamp,
)
