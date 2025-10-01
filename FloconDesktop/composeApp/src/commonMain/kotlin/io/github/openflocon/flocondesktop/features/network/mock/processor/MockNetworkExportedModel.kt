package io.github.openflocon.flocondesktop.features.network.mock.processor

import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class MockNetworkExportedModel(
    val id: String,
    val isEnabled: Boolean,
    val expectation: Expectation,
    val response: Response,
    val isShared: Boolean,
) {
    @Serializable
    data class Expectation(
        val urlPattern: String, // a regex
        val method: String, // can be get, post, put, ... or a wildcard *
    )

    @Serializable
    sealed interface Response {
        val delay: Long

        @Serializable
        data class Body(
            val httpCode: Int,
            val body: String,
            val mediaType: String,
            override val delay: Long,
            val headers: Map<String, String>,
        ) : Response

        @Serializable
        data class Exception(
            override val delay: Long,
            val classPath: String,
        ) : Response
    }
}

fun MockNetworkDomainModel.toExportedModel(): MockNetworkExportedModel = MockNetworkExportedModel(
    id = this.id,
    isEnabled = this.isEnabled,
    isShared = this.isShared,
    expectation = this.expectation.toExportedModel(),
    response = this.response.toExportedModel()
)

fun MockNetworkDomainModel.Expectation.toExportedModel(): MockNetworkExportedModel.Expectation =
    MockNetworkExportedModel.Expectation(
        urlPattern = this.urlPattern,
        method = this.method
    )

fun MockNetworkDomainModel.Response.toExportedModel(): MockNetworkExportedModel.Response =
    when (this) {
        is MockNetworkDomainModel.Response.Body -> MockNetworkExportedModel.Response.Body(
            httpCode = this.httpCode,
            body = this.body,
            mediaType = this.mediaType,
            delay = this.delay,
            headers = this.headers
        )
        is MockNetworkDomainModel.Response.Exception -> MockNetworkExportedModel.Response.Exception(
            delay = this.delay,
            classPath = this.classPath
        )
    }

fun MockNetworkExportedModel.toDomainModel(): MockNetworkDomainModel = MockNetworkDomainModel(
    id = this.id,
    isEnabled = this.isEnabled,
    isShared = this.isShared,
    expectation = this.expectation.toDomainModel(),
    response = this.response.toDomainModel()
)

fun MockNetworkExportedModel.Expectation.toDomainModel(): MockNetworkDomainModel.Expectation =
    MockNetworkDomainModel.Expectation(
        urlPattern = this.urlPattern,
        method = this.method
    )

fun MockNetworkExportedModel.Response.toDomainModel(): MockNetworkDomainModel.Response =
    when (this) {
        is MockNetworkExportedModel.Response.Body -> MockNetworkDomainModel.Response.Body(
            httpCode = this.httpCode,
            body = this.body,
            mediaType = this.mediaType,
            delay = this.delay,
            headers = this.headers
        )
        is MockNetworkExportedModel.Response.Exception -> MockNetworkDomainModel.Response.Exception(
            delay = this.delay,
            classPath = this.classPath
        )
    }
