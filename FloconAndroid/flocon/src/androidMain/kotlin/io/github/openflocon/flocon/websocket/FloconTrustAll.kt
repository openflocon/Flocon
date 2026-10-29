package io.github.openflocon.flocon.websocket

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * The Flocon desktop serves a self signed certificate: the channel is encrypted (so no cleartext traffic is needed)
 * but the certificate is not verified. Only used by the clients talking to the Flocon desktop.
 */
private object TrustAllManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}

internal fun OkHttpClient.Builder.trustFloconDesktopCertificate(): OkHttpClient.Builder {
    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(TrustAllManager), SecureRandom())
    }
    return sslSocketFactory(sslContext.socketFactory, TrustAllManager)
        .hostnameVerifier { _, _ -> true }
}
