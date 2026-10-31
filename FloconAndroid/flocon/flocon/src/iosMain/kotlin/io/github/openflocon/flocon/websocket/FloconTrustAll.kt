package io.github.openflocon.flocon.websocket

import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.create
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust

/**
 * The Flocon desktop serves a self signed certificate: the channel is encrypted (so no cleartext traffic is needed)
 * but the certificate is not verified. Only used by the clients talking to the Flocon desktop.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun DarwinClientEngineConfig.trustFloconDesktopCertificate() {
    handleChallenge { _, _, challenge, completionHandler ->
        val serverTrust = challenge.protectionSpace.serverTrust
        if (challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust && serverTrust != null) {
            completionHandler(NSURLSessionAuthChallengeUseCredential.convert(), NSURLCredential.credentialForTrust(serverTrust))
        } else {
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.convert(), null)
        }
    }
}
