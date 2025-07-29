package com.florent37.flocondesktop.features.network.data

import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestInfos
import kotlin.time.Clock

object FloconHttpRequestGenerator {
    /**
     * Génère une liste de fausses instances de FloconHttpRequestDataModel en une seule méthode,
     * sans l'utilisation de plusieurs listes pré-définies.
     * Les données sont générées dynamiquement en fonction de l'index.
     *
     * @param count Le nombre d'instances à générer.
     * @return Une liste de FloconHttpRequestDataModel générées.
     */
    fun generateDynamicFakeFloconHttpRequests(count: Int): List<FloconHttpRequestDomainModel> {
        require(count >= 0) { "Count must be a non-negative value." }

        return List(count) { index ->
            val urlScheme = if (index % 2 == 0) "https" else "http"
            val domain =
                when (index % 3) {
                    0 -> "api.example.com"
                    1 -> "data.service.net"
                    else -> "local.dev"
                }
            val path =
                when (index % 4) {
                    0 -> "/users"
                    1 -> "/products"
                    2 -> "/orders"
                    else -> "/networkStatusUi"
                }

            val method =
                when (index % 5) {
                    0 -> "GET"
                    1 -> "POST"
                    2 -> "PUT"
                    3 -> "DELETE"
                    else -> "PATCH"
                }

            val contentType = if (index % 3 == 0) "application/json" else "text/plain"
            val acceptType = if (index % 2 == 0) "application/xml" else "application/json"

            val requestBodyContent =
                if (index % 4 != 3) { // 30% chance of being null
                    if (index % 2 == 0) {
                        """{"id": ${index + 1}, "name": "Item_$index"}"""
                    } else {
                        "request data for index $index"
                    }
                } else {
                    null
                }

            val responseBodyContent =
                if (index % 5 != 4) { // 20% chance of being null
                    if (index % 2 == 0) {
                        """{"networkStatusUi": "success", "message": "Generated for $index"}"""
                    } else {
                        "Response text for request $index"
                    }
                } else {
                    null
                }

            FloconHttpRequestInfos(
                url = "$urlScheme://$domain$path/${index + 1}",
                method = method,
                startTime = Clock.System.now()
                    .toEpochMilliseconds() - (index * 500L), // Temps de démarrage décroissant
                durationMs = (100.0 + (index * 25.0)), // Durée croissante
                request = FloconHttpRequestInfos.Request(
                    headers =
                    mapOf(
                        "Content-Type" to contentType,
                        "Authorization" to "Bearer_token_$index",
                        "X-Request-ID" to "req-$index",
                    ),
                    body = requestBodyContent,
                    byteSize = 300,
                ),
                response = FloconHttpRequestInfos.Response(
                    body = responseBodyContent,
                    httpCode = 200,
                    byteSize = 1500,
                    headers =
                    mapOf(
                        "Content-Type" to acceptType,
                        "Cache-Control" to "no-cache",
                        "X-Response-ID" to "res-$index",
                    ),
                ),
            ).let {
                FloconHttpRequestDomainModel(uuid = index.toString(), infos = it)
            }
        }
    }
}
