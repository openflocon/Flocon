package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.network.model.MockNetworkResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal class MockNetworkResponseDataModel(
    val expectation: Expectation,
    val response: Response,
) {
    @Serializable
    class Expectation(
        val urlPattern: String, // a regex
        val method: String, // can be get, post, put, ... or a wildcard *
    )

    @Serializable
    class Response(
        val httpCode: Int?,
        val body: String?,
        val mediaType: String?,
        val delay: Long?,
        val headers: Map<String, String>?,
        val errorException: String?,
    )
}


internal fun parseMockResponses(jsonString: String): List<MockNetworkResponse> {
    try {
        val remote = FloconEncoder.json.decodeFromString<List<MockNetworkResponseDataModel>>(jsonString)
        return remote.mapNotNull {
            it.toDomain()
        }
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network parsing issue", t)
        return emptyList()
    }
}

internal fun MockNetworkResponseDataModel.toDomain(): MockNetworkResponse? {
    return MockNetworkResponse(
        expectation = MockNetworkResponse.Expectation(
            urlPattern = expectation.urlPattern,
            method = expectation.method,
        ),
        response = this.mapResponseToDomain() ?: return null
    )
}

private fun MockNetworkResponseDataModel.mapResponseToDomain(): MockNetworkResponse.Response? {
    return response.run {
        when {
            errorException != null -> MockNetworkResponse.Response.ErrorThrow(
                classPath = errorException,
                delay = delay ?: 0L,
            )

            httpCode != null -> MockNetworkResponse.Response.Body(
                httpCode = httpCode,
                body = body ?: "",
                delay = delay ?: 0L,
                mediaType = mediaType ?: "",
                headers = headers ?: emptyMap()
            )

            else -> run {
                FloconLogger.logError("error parsing mock response", null)
                return@run null
            }
        }
    }
}


internal fun writeMockResponsesToJson(mocks: List<MockNetworkResponse>): String {
    return try {
        FloconEncoder.json.encodeToString(mocks.map { it.toRemote() })
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock network writing issue", t)
        return "[]"
    }
}

private fun MockNetworkResponse.toRemote(): MockNetworkResponseDataModel? {
    return MockNetworkResponseDataModel(
        expectation = MockNetworkResponseDataModel.Expectation(
            urlPattern = expectation.urlPattern,
            method = expectation.method,
        ),
        response = mapResponseToRemote(),
    )
}

private fun MockNetworkResponse.mapResponseToRemote(): MockNetworkResponseDataModel.Response {
    return when (val response = this.response) {
        is MockNetworkResponse.Response.ErrorThrow -> MockNetworkResponseDataModel.Response(
            errorException = response.classPath,
            delay = response.delay,
            body = null,
            headers = null,
            httpCode = null,
            mediaType = null,
        )

        is MockNetworkResponse.Response.Body -> MockNetworkResponseDataModel.Response(
            errorException = null,
            delay = response.delay,
            body = response.body,
            headers = response.headers,
            httpCode = response.httpCode,
            mediaType = response.mediaType,
        )
    }
}
