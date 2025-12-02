package io.github.openflocon.data.local.network.utils

import com.opencsv.CSVReader
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.common.time.formatDuration
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Locale

private const val NUMBER_OF_COLUMNS_CSV_NETWORK = 20

internal fun importNetworkCallsFromCsv(file: File, appInstance: AppInstance): List<FloconNetworkCallDomainModel> {
    val lines = readCsv(file)
    if (lines.isEmpty()) return emptyList()

    val rows = lines.drop(1) // skip header

    return rows.mapNotNull { cols ->
        try {
            if (cols.size < NUMBER_OF_COLUMNS_CSV_NETWORK) return@mapNotNull null
            createDomainCallFromCsv(cols, appInstance = appInstance)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}

internal fun readCsv(file: File): List<Array<String>> {
    CSVReader(FileReader(file)).use { reader ->
        return reader.readAll()
    }
}

private fun parseHeaders(raw: String): Map<String, String> {
    if (raw.isBlank()) return emptyMap()
    return raw.split(" | ").mapNotNull {
        val parts = it.split(": ", limit = 2)
        if (parts.size == 2) parts[0] to parts[1] else null
    }.toMap()
}

private fun createDomainCallFromCsv(
    columns: Array<String>,
    appInstance: AppInstance
): FloconNetworkCallDomainModel {
    var index = 0
    val type = columns[index++]
    val url = columns[index++]
    val method = columns[index++]
    val startTimeStr = columns[index++]
    val durationMs = columns[index++].toDoubleOrNull() ?: 0.0
    val status = columns[index++]
    val httpCode = columns[index++].toIntOrNull()
    val id = columns[index++]
    val grpcStatus = columns[index++]
    val requestBodyByteSize = columns[index++].toLongOrNull() ?: 0L
    val responseBodySize = columns[index++].toLongOrNull() ?: 0L
    val issue = columns[index++]
    val isMocked = columns[index++].lowercase()?.toBooleanStrictOrNull() ?: false
    val domainFormatted = columns[index++]
    val queryFormatted = columns[index++]
    val statusFormatted = columns[index++]
    val isImage = columns[index++].lowercase().toBooleanStrictOrNull() ?: false
    val requestBody = columns[index++]
    val requestHeaders = parseHeaders(columns[index++])
    val responseBody = columns[index++]
    val responseHeaders = parseHeaders(columns[index++])

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    val startTimestamp = dateFormat.parse(startTimeStr)?.time ?: 0L

    val reqSpec = when (type) {
        "HTTP" -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
        "GraphQL" -> FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl("", "")
        "gRPC" -> FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc
        "websocket" -> FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket(event = status)
        else -> FloconNetworkCallDomainModel.Request.SpecificInfos.Http
    }

    val response = when (status) {
        "Success" -> FloconNetworkCallDomainModel.Response.Success(
            durationMs = durationMs,
            durationFormatted = formatDuration(durationMs),
            contentType = null,
            body = responseBody,
            headers = responseHeaders,
            byteSize = responseBodySize,
            byteSizeFormatted = ByteFormatter.formatBytes(responseBodySize),
            specificInfos =
            when {
                httpCode != null -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(
                    httpCode
                )

                grpcStatus.isNotBlank() -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc(
                    grpcStatus
                )

                else -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(200)
            },
            isImage = isImage,
            statusFormatted = statusFormatted
        )

        "Failure" -> FloconNetworkCallDomainModel.Response.Failure(
            durationMs = durationMs,
            durationFormatted = formatDuration(durationMs),
            issue = issue,
            statusFormatted = statusFormatted
        )

        else -> null
    }

    val request = FloconNetworkCallDomainModel.Request(
        url = url,
        startTime = startTimestamp,
        startTimeFormatted = formatTimestamp(startTimestamp),
        method = method,
        methodFormatted = method,
        headers = requestHeaders,
        body = requestBody,
        byteSize = requestBodyByteSize,
        byteSizeFormatted = ByteFormatter.formatBytes(requestBodyByteSize),
        isMocked = isMocked,
        specificInfos = reqSpec,
        domainFormatted = domainFormatted,
        queryFormatted = queryFormatted
    )

    return FloconNetworkCallDomainModel(
        callId = id,
        appInstance = appInstance,
        request = request,
        response = response
    )
}
