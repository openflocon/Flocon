package com.flocon.data.remote.server

import co.touchlab.kermit.Logger
import com.flocon.data.remote.models.ExportMetadata
import com.flocon.data.remote.models.FilterCriteria
import com.flocon.data.remote.models.NetworkCallExport
import com.flocon.data.remote.models.NetworkLogsExportResponse
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.networkExportRoutes(
    getNetworkCalls: suspend () -> List<FloconNetworkCallDomainModel>,
) {
    get("/api/export/network-logs") {
        try {
            val deviceId = call.request.queryParameters["deviceId"]
            val startTimestamp = call.request.queryParameters["startTimestamp"]?.toLongOrNull()
            val endTimestamp = call.request.queryParameters["endTimestamp"]?.toLongOrNull()

            val allCalls = getNetworkCalls()
            
            val filtered = allCalls.filter { call ->
                val matchesDevice = deviceId == null || call.appInstance.deviceId == deviceId
                val matchesStartTime = startTimestamp == null || call.request.startTime >= startTimestamp
                val matchesEndTime = endTimestamp == null || call.request.startTime <= endTimestamp
                
                matchesDevice && matchesStartTime && matchesEndTime
            }

            val exportedCalls = filtered.map { call ->
                NetworkCallExport(
                    callId = call.callId,
                    method = call.request.method,
                    url = call.request.url,
                    startTime = call.request.startTime,
                    startTimeFormatted = call.request.startTimeFormatted,
                    statusCode = when (val response = call.response) {
                        is FloconNetworkCallDomainModel.Response.Success -> 
                            when (val info = response.specificInfos) {
                                is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> info.httpCode
                                else -> null
                            }
                        else -> null
                    },
                    durationMs = call.response?.durationMs,
                    requestHeaders = call.request.headers,
                    responseHeaders = when (val response = call.response) {
                        is FloconNetworkCallDomainModel.Response.Success -> response.headers
                        else -> null
                    },
                    requestBody = call.request.body,
                    responseBody = when (val response = call.response) {
                        is FloconNetworkCallDomainModel.Response.Success -> response.body
                        else -> null
                    },
                    contentType = when (val response = call.response) {
                        is FloconNetworkCallDomainModel.Response.Success -> response.contentType
                        else -> null
                    },
                    deviceId = call.appInstance.deviceId,
                    appInstance = call.appInstance.appInstance,
                )
            }

            val response = NetworkLogsExportResponse(
                data = exportedCalls,
                metadata = ExportMetadata(
                    exportedAt = System.currentTimeMillis(),
                    totalItems = exportedCalls.size,
                    filteredBy = if (deviceId != null || startTimestamp != null || endTimestamp != null) {
                        FilterCriteria(
                            deviceId = deviceId,
                            startTimestamp = startTimestamp,
                            endTimestamp = endTimestamp,
                        )
                    } else {
                        null
                    },
                ),
            )

            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            Logger.e("Error exporting network logs", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (e.message ?: "Unknown error"))
            )
        }
    }
}