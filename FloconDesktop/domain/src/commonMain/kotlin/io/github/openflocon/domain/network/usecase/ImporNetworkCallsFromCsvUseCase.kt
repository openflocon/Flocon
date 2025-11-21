package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.repository.NetworkRepository
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

class ImportNetworkCallsFromCsvUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(ids: List<String>) : Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("no current device"))
        return try {
            showOpenFileDialog(
                dialogName = "Import Sql Query",
            )?.let {
                val calls = importCallsFromCsv(it, current.appInstance)
                networkRepository.addCalls(calls)
                Success(Unit)
            }
        } catch (t: Throwable) {
            Failure(t)
        }
    }

    suspend operator fun invoke() : String? {
        return showOpenFileDialog(
            dialogName = "Import Sql Query"
        )?.readText()
    }

    private fun showOpenFileDialog(dialogName: String): File? {
        val parentFrame = Frame()
        val dialog = FileDialog(parentFrame, dialogName, FileDialog.LOAD)

        dialog.isVisible = true // Bloque jusqu'à ce que la boîte de dialogue soit fermée

        val file = dialog.file
        val directory = dialog.directory

        parentFrame.dispose()

        return if (file != null && directory != null) {
            File(directory, file)
        } else {
            null
        }
    }

}

fun importCallsFromCsv(file: File, appInstance: AppInstance): List<FloconNetworkCallDomainModel> {
    val lines = file.readLines()
    if (lines.isEmpty()) return emptyList()

    val rows = lines.drop(1) // skip header

    return rows.mapNotNull { line ->
        if (line.isBlank()) return@mapNotNull null
        val cols = parseCsvLine(line)
        if (cols.size < 14) return@mapNotNull null
        createDomainCallFromCsv(cols, appInstance = appInstance)
    }
}

// Parse une ligne de CSV avec gestion des quotes
private fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false
    var i = 0

    while (i < line.length) {
        val c = line[i]
        when {
            c == '"' -> {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            c == ',' && !inQuotes -> {
                result.add(current.toString())
                current = StringBuilder()
            }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}

private fun parseHeaders(raw: String): Map<String, String> {
    if (raw.isBlank()) return emptyMap()
    return raw.split(" | ").mapNotNull {
        val parts = it.split(": ", limit = 2)
        if (parts.size == 2) parts[0] to parts[1] else null
    }.toMap()
}

// -------------------------------------------------------------
//  Reconstruction Domain
// -------------------------------------------------------------
private fun createDomainCallFromCsv(columns: List<String>, appInstance: AppInstance): FloconNetworkCallDomainModel {
    val type = columns[0]
    val url = columns[1]
    val method = columns[2]
    val startTimeStr = columns[3]
    val durationMs = columns[4].toDoubleOrNull() ?: 0.0
    val status = columns[5]
    val httpCode = columns[6].toIntOrNull()
    val grpcStatus = columns[7]
    val responseBodySize = columns[8].toLongOrNull() ?: 0L
    val issue = columns[9]
    val requestBody = columns[10]
    val requestHeaders = parseHeaders(columns[11])
    val responseBody = columns[12]
    val responseHeaders = parseHeaders(columns[13])

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
            durationFormatted = "", // TODO
            contentType = null,
            body = responseBody,
            headers = responseHeaders,
            byteSize = responseBodySize,
            byteSizeFormatted = "", // TODO
            specificInfos =
                when {
                    httpCode != null -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(httpCode)
                    grpcStatus.isNotBlank() -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc(grpcStatus)
                    else -> FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http(200)
                },
            isImage = false,
            statusFormatted = "" // TODO
        )
        "Failure" -> FloconNetworkCallDomainModel.Response.Failure(
            durationMs = durationMs,
            durationFormatted = "", // TODO
            issue = issue,
            statusFormatted = "" // TODO
        )
        else -> null
    }

    val request = FloconNetworkCallDomainModel.Request(
        url = url,
        startTime = startTimestamp,
        startTimeFormatted = startTimeStr,
        method = method,
        methodFormatted = method,
        headers = requestHeaders,
        body = requestBody,
        byteSize = requestBody.length.toLong(),
        byteSizeFormatted = "", // TODO
        isMocked = false,
        specificInfos = reqSpec,
        domainFormatted = "", // TODO
        queryFormatted = "" // TODO
    )

    return FloconNetworkCallDomainModel(
        callId = Uuid.random().toString(),
        appInstance = appInstance,
        request = request,
        response = response
    )
}