package io.github.openflocon.flocon.myapplication

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DummyHttpCaller(val client: OkHttpClient) {

    fun call() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/todos/1")
                    .get()
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException
                    ) {

                    }

                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {
                        Log.d("NET_NET", response.body?.string() ?: "")
                    }

                })
                Log.d("NET_NET", "success")
            } catch (t: Throwable) {
                Log.e("NET_NET", t.message, t)
            }
        }
    }

    fun callGzip() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://httpbin.org/gzip")
                    .addHeader("Accept-Encoding", "gzip")
                    .get()
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException
                    ) {

                    }

                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {
                        Log.d("NET_NET", response.body?.string() ?: "")
                    }

                })
                Log.d("NET_NET", "success")
            } catch (t: Throwable) {
                Log.e("NET_NET", t.message, t)
            }
        }
    }
}