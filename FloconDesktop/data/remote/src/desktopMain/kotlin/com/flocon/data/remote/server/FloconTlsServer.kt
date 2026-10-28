package com.flocon.data.remote.server

import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import java.net.InetAddress
import java.security.KeyStore

private const val KEY_ALIAS = "flocon"
private const val KEY_PASSWORD = "flocon"

/**
 * The SDKs connect through TLS (wss / https) so apps don't need to allow cleartext traffic.
 * The certificate is self signed and is not verified by the SDKs, it only exists to encrypt the channel.
 */
private val floconKeyStore: KeyStore by lazy {
    buildKeyStore {
        certificate(KEY_ALIAS) {
            password = KEY_PASSWORD
            domains = listOf("localhost")
            ipAddresses = listOf(InetAddress.getByName("127.0.0.1"))
            keySizeInBits = 2048
            daysValid = 3650
        }
    }
}

internal fun embeddedTlsServer(
    port: Int,
    module: Application.() -> Unit,
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> = embeddedServer(
    factory = Netty,
    configure = {
        sslConnector(
            keyStore = floconKeyStore,
            keyAlias = KEY_ALIAS,
            // ktor wipes the arrays it receives, so give it a fresh one each time
            keyStorePassword = { KEY_PASSWORD.toCharArray() },
            privateKeyPassword = { KEY_PASSWORD.toCharArray() },
        ) {
            this.port = port
        }
    },
    module = module,
)
