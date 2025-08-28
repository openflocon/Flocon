package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.repository.NetworkRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

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
        "Type",
        "URL",
        "Method",
        "Start Time",
        "Duration (ms)",
        "Status",
        "Http Code",
        "Body Size (bytes)",
        "Issue",
        "Request Body",
        "Request Headers",
        "Response Body",
        "Response Headers"
    )
    file.writeText(headerList.joinToString(separator = ",", postfix = "\n"))

    this.forEach { call ->
        val durationMs = (call.response?.durationMs?.roundToInt()) ?: 0
        val status = when (call.response) {
            is FloconNetworkCallDomainModel.Response.Success -> "Success"
            is FloconNetworkCallDomainModel.Response.Failure -> "Failure"
            null -> "Pending"
        }
        val httpResponse = when (call.response) {
            is FloconNetworkCallDomainModel.Response.Success -> call.response.specificInfos.httpCode().toString()
            is FloconNetworkCallDomainModel.Response.Failure -> null
            null -> null
        }
        val issue = (call.response as? FloconNetworkCallDomainModel.Response.Failure)?.issue ?: ""
        val bodySize = call.response?.let {
            when (it) {
                is FloconNetworkCallDomainModel.Response.Success -> it.byteSize
                is FloconNetworkCallDomainModel.Response.Failure -> 0L
            }
        } ?: call.request.byteSize


        val requestBody = call.request.body ?: ""

        val responseBody : String? = when(call.response) {
            is FloconNetworkCallDomainModel.Response.Success -> call.response.body
            is FloconNetworkCallDomainModel.Response.Failure -> null
            null -> null
        } ?: ""

        // Utilisation d'un point-virgule comme séparateur pour les en-têtes
        val requestHeadersString = call.request.headers.map { (key, value) ->
            "$key: $value"
        }.joinToString(separator = " | ")

        val responseHeadersString = (call.response as? FloconNetworkCallDomainModel.Response.Success)?.headers?.map { (key, value) ->
            "$key: $value"
        }?.joinToString(separator = " | ") ?: ""

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val startDate = Date(call.request.startTime)
        val formattedTime = dateFormat.format(startDate)

        val type = when(call.request.specificInfos) {
            is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "HTTP"
            is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> "GraphQL"
            is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> "gRPC"
        }

        val dataList = listOf(
            "\"$type\"",
            "\"${call.request.url}\"",
            "\"${call.request.method}\"",
            "\"$formattedTime\"",
            "\"$durationMs\"",
            "\"$status\"",
            "\"$httpResponse\"",
            "\"$bodySize\"",
            "\"$issue\"",
            "\"$requestBody\"",
            "\"$requestHeadersString\"",
            "\"$responseBody\"",
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
