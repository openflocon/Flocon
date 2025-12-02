package io.github.openflocon.data.local.network.utils

import com.opencsv.CSVWriter
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.models.isImage
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.math.roundToInt

internal fun List<FloconNetworkCallDomainModel>.exportToCsv(file: File) {
    CSVWriter(FileWriter(file)).use { writer ->

        val headerList = listOf(
            "Type",
            "URL",
            "Method",
            "Start Time",
            "Duration (ms)",
            "Status",
            "Http Code",
            "ID",
            "grpcStatus",
            "Request Body Size (bytes)",
            "Response Body Size (bytes)",
            "Issue",
            "Mocked",
            "DomainFormatted",
            "QueryFormatted",
            "StatusFormatted",
            "IsImage",
            "Request Body",
            "Request Headers",
            "Response Body",
            "Response Headers"
        )
        writer.writeNext(headerList.toTypedArray())

        this.forEach { call ->
            val durationMs = (call.response?.durationMs?.roundToInt()) ?: 0
            val status = when (call.response) {
                is FloconNetworkCallDomainModel.Response.Success -> "Success"
                is FloconNetworkCallDomainModel.Response.Failure -> "Failure"
                null -> when (val s = call.request.specificInfos) {
                    is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> s.event
                    else -> "Pending"
                }
            }
            val httpCode = when (val r = call.response) {
                is FloconNetworkCallDomainModel.Response.Success -> r.specificInfos.httpCode()
                    .toString()

                is FloconNetworkCallDomainModel.Response.Failure -> null
                null -> null
            }
            val issue =
                (call.response as? FloconNetworkCallDomainModel.Response.Failure)?.issue ?: ""

            val requestBodyByteSize = call.request.byteSize

            val responseBodySize = call.response?.let {
                when (it) {
                    is FloconNetworkCallDomainModel.Response.Success -> it.byteSize
                    is FloconNetworkCallDomainModel.Response.Failure -> 0L
                }
            } ?: 0L

            val grpcStatus = when (val r = call.response) {
                is FloconNetworkCallDomainModel.Response.Success -> when (
                    val s =
                        r.specificInfos
                ) {
                    is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> s.grpcStatus
                    else -> null
                }

                is FloconNetworkCallDomainModel.Response.Failure -> null
                null -> null
            } ?: ""

            val requestBody = call.request.body ?: ""

            val responseBody: String? = when (val r = call.response) {
                is FloconNetworkCallDomainModel.Response.Success -> r.body
                is FloconNetworkCallDomainModel.Response.Failure -> null
                null -> null
            } ?: ""

            // Utilisation d'un point-virgule comme séparateur pour les en-têtes
            val requestHeadersString = call.request.headers.map { (key, value) ->
                "$key: $value"
            }.joinToString(separator = " | ")

            val responseHeadersString =
                (call.response as? FloconNetworkCallDomainModel.Response.Success)?.headers?.map { (key, value) ->
                    "$key: $value"
                }?.joinToString(separator = " | ") ?: ""

            val statusFormatted = call.response?.statusFormatted ?: ""

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val startDate = Date(call.request.startTime)
            val formattedTime = dateFormat.format(startDate)

            val type = when (call.request.specificInfos) {
                is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> "HTTP"
                is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> "GraphQL"
                is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> "gRPC"
                is FloconNetworkCallDomainModel.Request.SpecificInfos.WebSocket -> "websocket"
            }

            val isImage = call.response?.isImage() ?: false

            val dataList = listOf(
                type,
                call.request.url,
                call.request.method,
                formattedTime,
                durationMs.toString(),
                status,
                httpCode ?: "",
                call.callId,
                grpcStatus,
                requestBodyByteSize.toString(),
                responseBodySize.toString(),
                issue,
                call.request.isMocked.toString(),
                call.request.domainFormatted,
                call.request.queryFormatted,
                statusFormatted,
                isImage.toString(),
                requestBody,
                requestHeadersString,
                responseBody,
                responseHeadersString,
            )
            writer.writeNext(dataList.toTypedArray())
        }
    }
}
