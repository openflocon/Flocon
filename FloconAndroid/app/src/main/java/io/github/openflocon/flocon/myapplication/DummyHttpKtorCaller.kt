package io.github.openflocon.flocon.myapplication

import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Socket
import java.security.cert.X509Certificate
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedTrustManager

object DummyHttpKtorCaller {

    val client = HttpClient(CIO) {
        install(FloconKtorPlugin)

        // Configuration du moteur CIO
        engine {
            https {
                // Attention : ceci désactive la vérification des certificats SSL/TLS.
                // Utilisez-le uniquement pour le développement ou des serveurs de test.
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

    fun call() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.get("https://jsonplaceholder.typicode.com/todos/1")

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