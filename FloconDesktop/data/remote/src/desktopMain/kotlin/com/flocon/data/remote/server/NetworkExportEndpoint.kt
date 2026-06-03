package com.flocon.data.remote.server

import co.touchlab.kermit.Logger
import com.flocon.data.remote.models.ExportMetadata
import com.flocon.data.remote.models.FilterCriteria
import com.flocon.data.remote.models.NetworkCallExport
import com.flocon.data.remote.models.NetworkLogsExportResponse
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

fun Route.networkExportRoutes(
    json: Json,
    // Filtering is done at DB level — timestamps passed through to the query
    getNetworkCalls: suspend (deviceId: String?, startTimestamp: Long?, endTimestamp: Long?) -> List<Pair<String, FloconNetworkCallDomainModel>>,
) {
    // 1. GET /api/network-logs — all calls from all devices
    get("/api/network-logs") {
        val calls = getNetworkCalls(null, null, null)
        call.respondJson(json, calls, deviceId = null, startTimestamp = null, endTimestamp = null)
    }

    // 2. GET /api/network-logs/{deviceId} — all calls for a specific device
    get("/api/network-logs/{deviceId}") {
        val deviceId = call.parameters["deviceId"]
        val calls = getNetworkCalls(deviceId, null, null)
        call.respondJson(json, calls, deviceId = deviceId, startTimestamp = null, endTimestamp = null)
    }

    // 3. GET /api/network-logs/{deviceId}/filter?startTimestamp=&endTimestamp= — device + time range
    get("/api/network-logs/{deviceId}/filter") {
        val deviceId = call.parameters["deviceId"]
        val startTimestamp = call.request.queryParameters["startTimestamp"]?.toLongOrNull()
        val endTimestamp = call.request.queryParameters["endTimestamp"]?.toLongOrNull()

        val calls = getNetworkCalls(deviceId, startTimestamp, endTimestamp)
        call.respondJson(json, calls, deviceId = deviceId, startTimestamp = startTimestamp, endTimestamp = endTimestamp)
    }
}

private suspend fun ApplicationCall.respondJson(
    json: Json,
    calls: List<Pair<String, FloconNetworkCallDomainModel>>,
    deviceId: String?,
    startTimestamp: Long?,
    endTimestamp: Long?,
) {
    try {
        val exportedCalls = calls.map { (storedDeviceId, networkCall) ->
            NetworkCallExport(
                callId = networkCall.callId,
                method = networkCall.request.method,
                url = networkCall.request.url,
                startTime = networkCall.request.startTime,
                startTimeFormatted = networkCall.request.startTimeFormatted,
                statusCode = when (val response = networkCall.response) {
                    is FloconNetworkCallDomainModel.Response.Success ->
                        when (val info = response.specificInfos) {
                            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> info.httpCode
                            else -> null
                        }
                    else -> null
                },
                durationMs = networkCall.response?.durationMs,
                requestHeaders = networkCall.request.headers,
                responseHeaders = when (val response = networkCall.response) {
                    is FloconNetworkCallDomainModel.Response.Success -> response.headers
                    else -> null
                },
                requestBody = networkCall.request.body,
                responseBody = when (val response = networkCall.response) {
                    is FloconNetworkCallDomainModel.Response.Success -> response.body
                    else -> null
                },
                contentType = when (val response = networkCall.response) {
                    is FloconNetworkCallDomainModel.Response.Success -> response.contentType
                    else -> null
                },
                deviceId = storedDeviceId,
                appInstance = networkCall.appInstance,
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
                } else null,
            ),
        )

        respondText(
            json.encodeToString(NetworkLogsExportResponse.serializer(), response),
            ContentType.Application.Json,
            HttpStatusCode.OK,
        )
    } catch (e: Exception) {
        Logger.e("Error exporting network logs", e)
        respondText(
            json.encodeToString(
                MapSerializer(serializer<String>(), serializer<String>()),
                mapOf("error" to (e.message ?: "Unknown error")),
            ),
            ContentType.Application.Json,
            HttpStatusCode.InternalServerError,
        )
    }
}
