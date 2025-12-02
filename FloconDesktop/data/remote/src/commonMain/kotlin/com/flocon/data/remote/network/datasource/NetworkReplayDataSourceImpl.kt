@file:OptIn(ExperimentalUuidApi::class)

package com.flocon.data.remote.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkReplayDataSource
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.common.time.formatDuration
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel.Response.Success.SpecificInfos
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.util.toMap
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class NetworkReplayDataSourceImpl : NetworkReplayDataSource {

    private val client = HttpClient()

    override suspend fun replay(request: FloconNetworkCallDomainModel): FloconNetworkCallDomainModel {
        val startTime = Clock.System.now().toEpochMilliseconds()
        val response = try {
            val result = client.request(request.request.url) {
                method = HttpMethod.parse(request.request.method)
                request.request.headers.forEach { (key, value) ->
                    headers.append(key, value)
                }
                request.request.body?.let {
                    setBody(TextContent(it, io.ktor.http.ContentType.Application.Json)) // Assuming JSON for now
                }
            }
            val endTime = Clock.System.now().toEpochMilliseconds()
            val duration = (endTime - startTime).toDouble()
            val body = result.bodyAsText()
            val size = body.length.toLong()

            FloconNetworkCallDomainModel.Response.Success(
                headers = result.headers.toMap().mapValues { it.value.joinToString(",") },
                body = body,
                specificInfos = SpecificInfos.Http(
                    httpCode = result.status.value
                ),
                durationMs = duration,
                durationFormatted = formatDuration(duration),
                byteSize = size,
                byteSizeFormatted = ByteFormatter.formatBytes(size),
                isImage = false, // TODO: check content type
                statusFormatted = result.status.value.toString(),
                contentType = result.headers["Content-Type"]
            )
        } catch (e: Exception) {
            val endTime = Clock.System.now().toEpochMilliseconds()
            val duration = (endTime - startTime).toDouble()
            FloconNetworkCallDomainModel.Response.Failure(
                issue = e.message ?: "Unknown error",
                durationMs = duration,
                durationFormatted = formatDuration(duration),
                statusFormatted = "Error"
            )
        }

        return request.copy(
            callId = Uuid.random().toString(),
            request = request.request.copy(
                startTime = startTime,
                startTimeFormatted = formatTimestamp(startTime),
            ),
            response = response,
            isReplayed = true,
        )
    }
}
