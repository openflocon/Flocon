package io.github.openflocon.flocon.myapplication.multi

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

/** Each call returns a short summary of the response, displayed under the button that triggered it. */
object DummyHttpKtorCaller {

    lateinit var client: HttpClient

    fun initialize(httpClient: HttpClient) {
        client = httpClient
    }

    suspend fun callGet(): String =
        client.get("https://jsonplaceholder.typicode.com/todos/1").summary()

    suspend fun callPost(): String =
        client.post("https://jsonplaceholder.typicode.com/posts") {
            setBody("{ \"test\" : \"yes\" }")
        }.summary()

    suspend fun callNotFound(): String =
        client.get("https://jsonplaceholder.typicode.com/todos/9999999").summary()

    private suspend fun HttpResponse.summary(): String = "${status.value} · ${bodyAsText().length} chars"
}
