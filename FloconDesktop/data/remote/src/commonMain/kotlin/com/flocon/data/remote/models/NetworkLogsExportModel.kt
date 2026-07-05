package com.flocon.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkLogsExportResponse(
    val data: List<NetworkCallExport>,
    val metadata: ExportMetadata,
)

@Serializable
internal data class ExportMetadata(
    val exportedAt: Long,
    val totalItems: Int,
    val filteredBy: FilterCriteria?,
)

@Serializable
internal data class FilterCriteria(
    val deviceId: String? = null,
    val startTimestamp: Long? = null,
    val endTimestamp: Long? = null,
)

@Serializable
internal data class NetworkCallExport(
    val callId: String,
    val method: String,
    val url: String,
    val startTime: Long,
    val startTimeFormatted: String,
    val statusCode: Int? = null,
    val durationMs: Double? = null,
    val requestHeaders: Map<String, String>,
    val responseHeaders: Map<String, String>? = null,
    val requestBody: String? = null,
    val responseBody: String? = null,
    val contentType: String? = null,
    val deviceId: String,
    val appInstance: Long,
)