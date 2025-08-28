package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository
import java.io.File

class ExportNetworkCallsToCsvUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(ids: List<String>) : Either<Throwable, String> {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device id"))
        val requests = networkRepository.getRequests(
            deviceIdAndPackageName = deviceIdAndPackageName,
            ids = ids
        )
        val fileName = "network_calls_${System.currentTimeMillis()}.csv"
        requests.exportToCsv(fileName = fileName)
        return Success(fileName)
    }
}

fun List<FloconNetworkCallDomainModel>.exportToCsv(fileName: String) {
    val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"
    val file = File(desktopPath, fileName)

    val headerList = listOf(
        "Call ID",
        "URL",
        "Method",
        "Start Time",
        "Duration (ms)",
        "Status",
        "Body Size (bytes)",
        "Issue",
        "Request Headers",
        "Response Headers"
    )
    file.writeText(headerList.joinToString(separator = ",", postfix = "\n"))

    this.forEach { call ->
        val durationMs = call.response?.durationMs ?: 0.0
        val status = when (call.response) {
            is FloconNetworkCallDomainModel.Response.Success -> "Success"
            is FloconNetworkCallDomainModel.Response.Failure -> "Failure"
            null -> "Pending"
        }
        val issue = (call.response as? FloconNetworkCallDomainModel.Response.Failure)?.issue ?: "N/A"
        val bodySize = call.response?.let {
            when (it) {
                is FloconNetworkCallDomainModel.Response.Success -> it.byteSize
                is FloconNetworkCallDomainModel.Response.Failure -> 0L
            }
        } ?: call.request.byteSize

        // Utilisation d'un point-virgule comme séparateur pour les en-têtes
        val requestHeadersString = call.request.headers.map { (key, value) ->
            "$key: $value"
        }.joinToString(separator = " | ")

        val responseHeadersString = (call.response as? FloconNetworkCallDomainModel.Response.Success)?.headers?.map { (key, value) ->
            "$key: $value"
        }?.joinToString(separator = " | ") ?: "N/A"

        val dataList = listOf(
            "\"${call.callId}\"",
            "\"${call.request.url}\"",
            "\"${call.request.method}\"",
            "\"${call.request.startTime}\"",
            "\"$durationMs\"",
            "\"$status\"",
            "\"$bodySize\"",
            "\"$issue\"",
            "\"$requestHeadersString\"",
            "\"$responseHeadersString\""
        )
        val escapedDataList = dataList.map { csvEscape(it) }
        file.appendText(escapedDataList.joinToString(separator = ",", postfix = "\n"))
    }
    println("Fichier CSV exporté avec succès sur le bureau : ${file.absolutePath}")
}

private fun csvEscape(text: String?): String {
    val nonNullText = text ?: ""
    val containsSpecialChars = nonNullText.contains(',')
    return if (containsSpecialChars) {
        val escapedText = nonNullText.replace("\"", "\"\"")
        "\"$escapedText\""
    } else {
        nonNullText
    }
}
