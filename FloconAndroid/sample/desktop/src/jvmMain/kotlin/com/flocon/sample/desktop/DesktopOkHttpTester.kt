package com.flocon.sample.desktop

import io.github.openflocon.flocon.okhttp.FloconOkhttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request

/** Calls through an [OkHttpClient] with the Flocon interceptor, to check it on the JVM. */
object DesktopOkHttpTester {

    private val client = OkHttpClient.Builder()
        .addInterceptor(FloconOkhttpInterceptor())
        .build()

    fun call(): String = execute("https://jsonplaceholder.typicode.com/todos/1")

    fun callGzip(): String = execute("https://httpbin.org/gzip", gzip = true)

    private fun execute(url: String, gzip: Boolean = false): String {
        val request = Request.Builder()
            .url(url)
            .apply { if (gzip) addHeader("Accept-Encoding", "gzip") }
            .get()
            .build()
        return client.newCall(request).execute().use { "HTTP ${it.code}" }
    }
}
