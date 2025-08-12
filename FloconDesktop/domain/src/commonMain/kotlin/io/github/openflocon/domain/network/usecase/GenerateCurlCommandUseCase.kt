package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

class GenerateCurlCommandUseCase {
    operator fun invoke(infos: FloconNetworkCallDomainModel): String {
        val url = infos.networkRequest.url
        val commandBuilder = StringBuilder("curl")

        // 1. Add HTTP Method
        commandBuilder.append(" -X ${infos.networkRequest.method}")

        // 2. Add Request Headers
        infos.networkRequest.headers.forEach { (key, value) ->
            // Escape double quotes within header values if they exist
            val escapedValue = value.replace("\"", "\\\"")
            commandBuilder.append(" -H \"$key: $escapedValue\"")
        }

        // 3. Add Request Body (if present)
        infos.networkRequest.body?.let { body ->
            if (body.isNotEmpty()) {
                // Escape single quotes within the body for shell compatibility
                // ' -> '\'' (closes current single quote, adds escaped single quote, reopens single quote)
                val escapedBody = body.replace("'", "'\\''")
                commandBuilder.append(" -d '$escapedBody'")
            }
        }

        // 4. Add URL (always last for better readability in the command line)
        // Ensure URL is quoted to handle special characters
        commandBuilder.append(" '${url}'")

        return commandBuilder.toString()
    }
}
