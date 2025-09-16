package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

internal fun parseMockResponses(jsonString: String): List<MockNetworkResponse> {
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

        val responseJson = jsonObject.getJSONObject("response")

        val delay = responseJson.getLong("delay")
        val errorException = responseJson.optString("errorException", "").takeIf { it.isNotBlank() && it != "null" }

        val response = errorException?.let {
            MockNetworkResponse.Response.ErrorThrow(
                classPath = it,
                delay = delay,
            )
        } ?: run {
            val httpCode = responseJson.getInt("httpCode")
            val body = responseJson.getString("body")
            val mediaType = responseJson.getString("mediaType")
            val headersJson = responseJson.getJSONObject("headers")
            val headers = buildMap<String, String> {
                val keys = headersJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    put(key = key, value = headersJson.getString(key))
                }
            }

            MockNetworkResponse.Response.Body(
                httpCode = httpCode,
                body = body,
                mediaType = mediaType,
                delay = delay,
                headers = headers
            )
        }

        MockNetworkResponse(expectation, response)
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network parsing issue", t)
        null
    }
}


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

private fun encodeMockNetworkResponse(mock: MockNetworkResponse): JSONObject {
    return try {
        val expectationJson = JSONObject().apply {
            put("urlPattern", mock.expectation.urlPattern)
            put("method", mock.expectation.method)
            // L'objet Pattern ne peut pas être sérialisé directement en JSON.
            // On le laisse de côté, il sera recréé lors du parsing.
        }

        val responseJson = JSONObject().apply {
            when(val response = mock.response) {
                is MockNetworkResponse.Response.ErrorThrow -> {
                    put("errorException", response.classPath)
                }
                is MockNetworkResponse.Response.Body -> {
                    val headersJson = JSONObject(response.headers)
                    put("httpCode", response.httpCode)
                    put("body", response.body)
                    put("mediaType", response.mediaType)
                    put("headers", headersJson)
                }
            }

            put("delay", mock.response.delay)
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
