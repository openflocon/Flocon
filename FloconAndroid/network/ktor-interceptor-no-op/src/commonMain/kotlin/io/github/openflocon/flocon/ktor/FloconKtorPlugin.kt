package io.github.openflocon.flocon.ktor

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

data class FloconNetworkIsImageParams(
    val request: HttpRequest,
    val response: HttpResponse,
    val responseContentType: String?,
)

class FloconKtorPluginConfig {
    var isImage: ((param: FloconNetworkIsImageParams) -> Boolean)? = null
    var shouldLog: ((request: HttpRequestBuilder) -> Boolean) = { true }
}

val FloconKtorPlugin = createClientPlugin("FloconKtorPlugin", ::FloconKtorPluginConfig) {
}

fun HttpClientConfig<*>.floconInterceptor() {
}