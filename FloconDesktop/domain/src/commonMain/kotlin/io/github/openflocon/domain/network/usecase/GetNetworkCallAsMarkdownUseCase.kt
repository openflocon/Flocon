package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.JsonFormatter
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.domain.network.models.responseByteSizeFormatted
import io.github.openflocon.domain.network.models.responseHeaders
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.firstOrNull

class GetNetworkCallAsMarkdownUseCase(
    private val networkRepository: NetworkRepository,
    private val jsonFormatter: JsonFormatter,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(callId: String): String? {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return null
        val call = networkRepository.observeRequest(
            deviceIdAndPackageName = deviceIdAndPackageName,
            requestId = callId
        ).firstOrNull() ?: return null


        return buildString {
            appendLine("### ${call.request.method} ${call.request.url}")
            appendLine()
            appendLine("**Status**: ${call.response?.statusFormatted ?: "Pending"}")
            appendLine("**Time**: ${call.request.startTimeFormatted}")
            appendLine("**Duration**: ${call.response?.durationFormatted ?: "-"}")
            appendLine("**Size**: ${call.responseByteSizeFormatted() ?: "-"}")
            appendLine()

            appendLine("#### Request Headers")
            appendLine("```")
            call.request.headers.forEach { (key, value) ->
                appendLine("$key: $value")
            }
            appendLine("```")
            appendLine()

            if (!call.request.body.isNullOrBlank()) {
                appendLine("#### Request Body")
                appendLine("```json")
                appendLine(jsonFormatter.toPrettyJson(call.request.body))
                appendLine("```")
                appendLine()
            }

            if (call.response != null) {
                appendLine("#### Response Headers")
                appendLine("```")
                call.responseHeaders()?.forEach { (key, value) ->
                    appendLine("$key: $value")
                }
                appendLine("```")
                appendLine()

                if (!call.responseBody().isNullOrBlank()) {
                    appendLine("#### Response Body")
                    appendLine("```json")
                    appendLine(call.responseBody()?.let {
                        jsonFormatter.toPrettyJson(it)
                    } )
                    appendLine("```")
                } else if (call.response is io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel.Response.Failure) {
                    appendLine("#### Error")
                    appendLine("```")
                    appendLine((call.response as io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel.Response.Failure).issue)
                    appendLine("```")
                }
            }
        }
    }
}
