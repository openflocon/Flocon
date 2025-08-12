package io.github.openflocon.flocon.ktor

import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.toByteArray
import java.nio.charset.StandardCharsets
import kotlin.coroutines.CoroutineContext


@OptIn(InternalAPI::class)
internal fun HttpResponse.cloneWithBody(newBody: ByteReadChannel): HttpResponse {
    return object : HttpResponse() {
        override val call: HttpClientCall = this@cloneWithBody.call

        override val coroutineContext: CoroutineContext = this@cloneWithBody.coroutineContext
        override val headers: Headers = this@cloneWithBody.headers
        override val requestTime: GMTDate = this@cloneWithBody.requestTime
        override val responseTime: GMTDate = this@cloneWithBody.responseTime
        override val status: HttpStatusCode = this@cloneWithBody.status
        override val version: HttpProtocolVersion = this@cloneWithBody.version
        override val rawContent: ByteReadChannel = newBody
    }
}

internal suspend fun extractAndReplaceRequestBody(request: HttpRequestBuilder): String? {
    val originalBody = request.body

    // If it's already a type that can be read multiple times (e.g., a String), no problem.
    if (originalBody is OutgoingContent.ByteArrayContent) {
        return originalBody.bytes().toString(StandardCharsets.UTF_8)
    }

    // If it's a data stream, we need to read and replace it.
    if (originalBody is OutgoingContent.ReadChannelContent) {
        val bytes = originalBody.readFrom().toByteArray()
        val bodyString = bytes.toString(StandardCharsets.UTF_8)

        // We replace the original body with a new one that contains the bytes we just read.
        // We create a new OutgoingContent that can be read again.
        request.setBody(ByteArrayContent(bytes))

        return bodyString
    }

    return when (originalBody) {
        is OutgoingContent.WriteChannelContent -> null // Still complex to handle
        is OutgoingContent.NoContent -> null
        else -> originalBody?.toString()
    }
}