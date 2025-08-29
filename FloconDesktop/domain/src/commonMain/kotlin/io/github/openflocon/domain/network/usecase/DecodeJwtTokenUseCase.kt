package io.github.openflocon.domain.network.usecase

import java.util.Base64

class DecodeJwtTokenUseCase {

    operator fun invoke(token: String) : String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            var payload = parts[1]

            // Corrige le padding manquant (JWT enlève souvent les "=")
            val padding = 4 - (payload.length % 4)
            if (padding in 1..3) {
                payload += "=".repeat(padding)
            }

            // Remplace les caractères URL-safe par ceux de Base64 standard
            payload = payload.replace('-', '+').replace('_', '/')

            val decodedBytes = Base64.getDecoder().decode(payload)
            String(decodedBytes, Charsets.UTF_8)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

}
