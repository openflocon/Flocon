package io.github.openflocon.flocon.myapplication

import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Socket
import java.security.cert.X509Certificate
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedTrustManager

object DummyHttpKtorCaller {

    val cioClient = HttpClient(CIO) {
        install(FloconKtorPlugin)

        engine {
            https {
                // for CIO we need to disable the trust manager for jsonplaceholder
                trustManager = object : X509ExtendedTrustManager() {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {}
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                }
            }
        }
    }

    val okHttpClient = HttpClient(OkHttp) {
        install(FloconKtorPlugin)
    }

    fun call() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = okHttpClient.post("https://jsonplaceholder.typicode.com/posts") {
                    setBody("{ \"test\" : \"yes\" }")
                }

                if (response.status.value in 200..299) {
                    val responseBody: String = response.body()
                    println("SUCCESS: $responseBody")
                } else {
                    println("FAILURE: ${response.status}")
                }
            } catch (t: Throwable) {
                println("ERROR: ${t.message}")
                t.printStackTrace()
            }
        }
    }
}