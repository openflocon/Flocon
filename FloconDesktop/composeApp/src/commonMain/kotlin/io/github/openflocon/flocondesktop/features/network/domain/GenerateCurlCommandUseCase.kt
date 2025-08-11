package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.domain.models.FloconHttpRequestDomainModel

class GenerateCurlCommandUseCase {
    operator fun invoke(infos: FloconHttpRequestDomainModel): String {
        val commandBuilder = StringBuilder("curl")

        // 1. Add HTTP Method
        commandBuilder.append(" -X ${infos.request.method}")

        // 2. Add Request Headers
        infos.request.headers.forEach { (key, value) ->
            // Escape double quotes within header values if they exist
            val escapedValue = value.replace("\"", "\\\"")
            commandBuilder.append(" -H \"$key: $escapedValue\"")
        }

        // 3. Add Request Body (if present)
        infos.request.body?.let { body ->
            if (body.isNotEmpty()) {
                // Escape single quotes within the body for shell compatibility
                // ' -> '\'' (closes current single quote, adds escaped single quote, reopens single quote)
                val escapedBody = body.replace("'", "'\\''")
                commandBuilder.append(" -d '$escapedBody'")
            }
        }

        // 4. Add URL (always last for better readability in the command line)
        // Ensure URL is quoted to handle special characters
        commandBuilder.append(" '${infos.url}'")

        return commandBuilder.toString()
    }
}
