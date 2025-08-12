package io.github.openflocon.flocon.ktor

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.FloconNetworkPlugin
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.delay
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.coroutines.CoroutineContext

val FloconKtorPlugin = createClientPlugin("FloconKtorPlugin") {

    val theClient = client

    // Interception des requêtes
    client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
        try {
            val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin
            if (floconNetworkPlugin == null) {
                proceed()
                return@intercept
            }

            val request = context
            val floconCallId = UUID.randomUUID().toString()
            val floconNetworkType = "http"
            val requestedAt = System.currentTimeMillis()

            // Lecture du body sans le consommer
            val requestBodyString = extractAndReplaceRequestBody(request)
            val requestSize = requestBodyString?.toByteArray(StandardCharsets.UTF_8)?.size?.toLong()
            val requestHeadersMap =
                request.headers.entries().associate { it.key to it.value.joinToString(",") }

            val mockConfig = findMock(request, floconNetworkPlugin)
            val isMocked = mockConfig != null

            val floconNetworkRequest = FloconNetworkRequest(
                url = request.url.toString(),
                method = request.method.value,
                startTime = requestedAt,
                headers = requestHeadersMap,
                body = requestBodyString,
                size = requestSize,
                isMocked = isMocked
            )

            floconNetworkPlugin.logRequest(
                FloconNetworkCallRequest(
                    floconCallId = floconCallId,
                    floconNetworkType = floconNetworkType,
                    isMocked = isMocked,
                    request = floconNetworkRequest
                )
            )

            if (isMocked) {
                val fakeCall = executeMock(client = theClient, request = request, mock = mockConfig)
                proceedWith(fakeCall)
                return@intercept
            }

            // Stocker métadonnées pour phase réponse
            request.attributes.put(FLOCON_CALL_ID_KEY, floconCallId)
            request.attributes.put(FLOCON_START_TIME_KEY, System.nanoTime())
            request.attributes.put(FLOCON_IS_MOCKED_KEY, isMocked)

            proceed()
        } catch (t: Throwable) {
            FloconLogger.logError("Ktor interceptor issue", t)
            proceed()
        }
    }

    // Interception des réponses
    client.receivePipeline.intercept(HttpReceivePipeline.After) { response ->
        val floconNetworkPlugin = FloconApp.instance?.client?.networkPlugin ?: return@intercept

        val call = response.call
        val request = call.request

        val floconCallId = request.attributes.getOrNull(FLOCON_CALL_ID_KEY) ?: return@intercept
        val startTime = request.attributes.getOrNull(FLOCON_START_TIME_KEY) ?: return@intercept
        val isMocked = request.attributes.getOrNull(FLOCON_IS_MOCKED_KEY) ?: false

        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1e6

        val originalBodyBytes = response.bodyAsChannel().toByteArray()
        val bodyString = originalBodyBytes.toString(StandardCharsets.UTF_8)
        val responseSize = originalBodyBytes.size.toLong()

        val responseHeadersMap =
            response.headers.entries().associate { it.key to it.value.joinToString(",") }
        val contentType = response.contentType()?.toString()
        val isImage = contentType?.startsWith("image/") == true

        val floconCallResponse = FloconNetworkResponse(
            httpCode = response.status.value,
            contentType = contentType,
            body = bodyString.takeUnless { isImage },
            headers = responseHeadersMap,
            size = responseSize,
            grpcStatus = null
        )

        floconNetworkPlugin.logResponse(
            FloconNetworkCallResponse(
                floconCallId = floconCallId,
                durationMs = durationMs,
                floconNetworkType = "http",
                isMocked = isMocked,
                response = floconCallResponse
            )
        )

        // as we consumed the body, we should inject a new one here
        val newResponse = response.cloneWithBody(ByteReadChannel(originalBodyBytes))
        proceedWith(newResponse)
    }
}


fun HttpClientConfig<*>.floconInterceptor() {
    install(FloconKtorPlugin)
}

// === Utilitaires ===

private val FLOCON_CALL_ID_KEY = AttributeKey<String>("floconCallId")
private val FLOCON_START_TIME_KEY = AttributeKey<Long>("floconStartTime")
private val FLOCON_IS_MOCKED_KEY = AttributeKey<Boolean>("floconIsMocked")

private fun findMock(
    request: HttpRequestBuilder,
    plugin: FloconNetworkPlugin,
): MockNetworkResponse? {
    for (mock in plugin.mocks) {
        val urlMatches = mock.expectation.pattern.matcher(request.url.toString()).matches()
        val methodMatches = mock.expectation.method == "*" ||
                mock.expectation.method.equals(request.method.value, ignoreCase = true)
        if (urlMatches && methodMatches) return mock
    }
    return null
}

@OptIn(InternalAPI::class)
private suspend fun executeMock(
    request: HttpRequestBuilder,
    client: HttpClient,
    mock: MockNetworkResponse
): HttpClientCall {
    if (mock.response.delay > 0) {
        delay(mock.response.delay)
    }

    val bodyBytes = mock.response.body.toByteArray()
    val headers = HeadersBuilder().apply {
        mock.response.headers.forEach { (name, value) -> append(name, value) }
    }.build()

    val responseData = HttpResponseData(
        statusCode = HttpStatusCode.fromValue(mock.response.httpCode),
        requestTime = GMTDate(),
        headers = headers,
        version = HttpProtocolVersion.HTTP_1_1,
        body = ByteReadChannel(bodyBytes),
        callContext = client.coroutineContext,
    )

    return HttpClientCall(client, request.build(), responseData)
}

private suspend fun extractRequestBody(body: Any?, charset: Charset = Charsets.UTF_8): String? {
    return when (body) {
        is OutgoingContent.ByteArrayContent -> body.bytes().toString(charset)
        is OutgoingContent.ReadChannelContent -> body.readFrom().readRemaining().readText(charset)
        is OutgoingContent.WriteChannelContent -> null // Complexe à dupliquer
        is OutgoingContent.NoContent -> null
        else -> body?.toString()
    }
}

// L'extension de la classe HttpResponse pourrait ressembler à ça :
@OptIn(InternalAPI::class)
private fun HttpResponse.cloneWithBody(newBody: ByteReadChannel): HttpResponse {
    return object : HttpResponse() {
        override val call: HttpClientCall = this@cloneWithBody.call

        //override val content: ByteReadChannel = newBody
        override val coroutineContext: CoroutineContext = this@cloneWithBody.coroutineContext
        override val headers: Headers = this@cloneWithBody.headers
        override val requestTime: GMTDate = this@cloneWithBody.requestTime
        override val responseTime: GMTDate = this@cloneWithBody.responseTime
        override val status: HttpStatusCode = this@cloneWithBody.status
        override val version: HttpProtocolVersion = this@cloneWithBody.version
        override val rawContent: ByteReadChannel = newBody
    }
}

private suspend fun extractAndReplaceRequestBody(request: HttpRequestBuilder): String? {
    val originalBody = request.body

    // Si c'est déjà un type qui peut être lu plusieurs fois (e.g. un String), pas de souci.
    if (originalBody is OutgoingContent.ByteArrayContent) {
        return originalBody.bytes().toString(StandardCharsets.UTF_8)
    }

    // Si c'est un flux de données, il faut le lire et le remplacer.
    if (originalBody is OutgoingContent.ReadChannelContent) {
        val bytes = originalBody.readFrom().toByteArray()
        val bodyString = bytes.toString(StandardCharsets.UTF_8)

        // On remplace le corps original par un nouveau qui contient les bytes lus.
        // On crée un nouveau OutgoingContent qui peut être lu.
        request.setBody(ByteArrayContent(bytes))

        return bodyString
    }

    return when (originalBody) {
        is OutgoingContent.WriteChannelContent -> null // Toujours complexe à gérer
        is OutgoingContent.NoContent -> null
        else -> originalBody?.toString()
    }
}