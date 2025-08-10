package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern


fun parseMockResponses(jsonString: String): List<MockNetworkResponse> {
    val mockResponses = mutableListOf<MockNetworkResponse>()
    try {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            decodeMockNetworkResponse(jsonObject)?.let {
                mockResponses.add(it)
            }
        }
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network parsing issue", t)
        return emptyList()
    }
    return mockResponses
}

private fun decodeMockNetworkResponse(jsonObject: JSONObject): MockNetworkResponse? {
    return try {
        val expectationJson = jsonObject.getJSONObject("expectation")
        val urlPattern = expectationJson.getString("urlPattern")
        val method = expectationJson.getString("method")
        val expectation = MockNetworkResponse.Expectation(
            urlPattern = urlPattern,
            pattern = Pattern.compile(urlPattern),
            method = method,
        )

        // Décoder l'objet response
        val responseJson = jsonObject.getJSONObject("response")
        val httpCode = responseJson.getInt("httpCode")
        val body = responseJson.getString("body")
        val mediaType = responseJson.getString("mediaType")
        val delay = responseJson.getLong("delay")

        // Décoder les headers
        val headersJson = responseJson.getJSONObject("headers")
        val headers = mutableMapOf<String, String>()
        val keys = headersJson.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            headers[key] = headersJson.getString(key)
        }

        val response = MockNetworkResponse.Response(
            httpCode = httpCode,
            body = body,
            mediaType = mediaType,
            delay = delay,
            headers = headers
        )

        MockNetworkResponse(expectation, response)
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network parsing issue", t)
        null
    }
}


/**
 * Sérialise une liste de MockNetworkResponse en une chaîne JSON.
 * @param mocks La liste des objets MockNetworkResponse à sérialiser.
 * @return La chaîne JSON représentant la liste des mocks, ou une chaîne vide en cas d'erreur.
 */
fun writeMockResponsesToJson(mocks: List<MockNetworkResponse>): JSONArray {
    val jsonArray = JSONArray()
    try {
        mocks.forEach { mock ->
            val jsonObject = encodeMockNetworkResponse(mock)
            jsonArray.put(jsonObject)
        }
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network writing issue", t)
    }
    return jsonArray
}

/**
 * Sérialise un MockNetworkResponse en un JSONObject.
 * @param mock L'objet MockNetworkResponse à sérialiser.
 * @return Un JSONObject représentant l'objet mock, ou un JSONObject vide en cas d'erreur.
 */
private fun encodeMockNetworkResponse(mock: MockNetworkResponse): JSONObject {
    return try {
        val expectationJson = JSONObject().apply {
            put("urlPattern", mock.expectation.urlPattern)
            put("method", mock.expectation.method)
            // L'objet Pattern ne peut pas être sérialisé directement en JSON.
            // On le laisse de côté, il sera recréé lors du parsing.
        }

        val headersJson = JSONObject(mock.response.headers)

        val responseJson = JSONObject().apply {
            put("httpCode", mock.response.httpCode)
            put("body", mock.response.body)
            put("mediaType", mock.response.mediaType)
            put("delay", mock.response.delay)
            put("headers", headersJson)
        }

        JSONObject().apply {
            put("expectation", expectationJson)
            put("response", responseJson)
        }
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network writing issue", t)
        JSONObject()
    }
}
