@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.ktor

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkCallResponse
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkRequest
import io.github.openflocon.flocon.plugins.network.model.FloconNetworkResponse
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocon.utils.currentTimeNanos
import io.ktor.client.call.save
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class FloconNetworkIsImageParams(
    val request: HttpRequest,
    val response: HttpResponse,
    val responseContentType: String?,
)

class FloconKtorPluginConfig {
    var isImage: ((param: FloconNetworkIsImageParams) -> Boolean)? = null
}

val FloconKtorPlugin = createClientPlugin("FloconKtorPlugin", ::FloconKtorPluginConfig) {
}

fun HttpClientConfig<*>.floconInterceptor() {
    install(FloconKtorPlugin)
}