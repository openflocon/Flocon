package io.github.openflocon.flocon.myapplication.multi

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DummyHttpKtorCaller {

    lateinit var client: HttpClient

    fun initialize(httpClient: HttpClient) {
        client = httpClient
    }

    fun callGet() {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val response = client.get("https://jsonplaceholder.typicode.com/todos/1")

                if (response.status.value in 200..299) {
                    val responseBody: String = response.body()
                    println("GET SUCCESS: $responseBody")
                } else {
                    println("GET FAILURE: ${response.status}")
                }
            } catch (t: Throwable) {
                println("GET ERROR: ${t.message}")
                t.printStackTrace()
            }
        }
    }

    fun callPost() {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val response = client.post("https://jsonplaceholder.typicode.com/posts") {
                    setBody("{ \"test\" : \"yes\" }")
                }

                if (response.status.value in 200..299) {
                    val responseBody: String = response.body()
                    println("POST SUCCESS: $responseBody")
                } else {
                    println("POST FAILURE: ${response.status}")
                }
            } catch (t: Throwable) {
                println("POST ERROR: ${t.message}")
                t.printStackTrace()
            }
        }
    }
}

