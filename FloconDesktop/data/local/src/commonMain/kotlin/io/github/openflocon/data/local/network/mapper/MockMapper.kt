package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.mock.MockNetworkExpectationEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json

fun MockNetworkDomainModel.toEntity(
    json: Json,
    deviceInfo: DeviceIdAndPackageNameDomainModel
): MockNetworkEntity? {
    val response = try {
        json.encodeToString(response.toEntity())
    } catch (t: Throwable) {
        t.printStackTrace()
        return null
    }
    return MockNetworkEntity(
        deviceId = deviceInfo.deviceId,
        packageName = deviceInfo.packageName,
        mockId = id,
        isEnabled = isEnabled,
        expectation = MockNetworkExpectationEmbedded(
            urlPattern = expectation.urlPattern,
            method = expectation.method,
        ),
        response = response,
    )
}

private fun MockNetworkDomainModel.Response.toEntity() : MockNetworkResponseEmbedded {
    return MockNetworkResponseEmbedded(
        delay = delay,
        type = when(this) {
            is MockNetworkDomainModel.Response.Body -> MockNetworkResponseEmbedded.Type.Body(
                httpCode = httpCode,
                body = body,
                mediaType = mediaType,
                headers = headers,
            )
            is MockNetworkDomainModel.Response.Exception -> MockNetworkResponseEmbedded.Type.Exception(
                classPath = classPath,
            )
        }
    )
}

fun MockNetworkEntity.toDomain(
    json: Json
): MockNetworkDomainModel? {
    val response = try {
        json.decodeFromString<MockNetworkResponseEmbedded>(response).toDomain()
    } catch (t: Throwable) {
        t.printStackTrace()
        return null
    }
    return MockNetworkDomainModel(
        id = mockId,
        isEnabled = isEnabled,
        expectation = MockNetworkDomainModel.Expectation(
            urlPattern = expectation.urlPattern,
            method = expectation.method,
        ),
        response = response,
    )
}

private fun MockNetworkResponseEmbedded.toDomain() : MockNetworkDomainModel.Response {
    return when(this.type) {
        is MockNetworkResponseEmbedded.Type.Body -> MockNetworkDomainModel.Response.Body(
            httpCode = this.type.httpCode,
            body = this.type.body,
            mediaType = this.type.mediaType,
            headers = this.type.headers,
            delay = this.delay,
        )
        is MockNetworkResponseEmbedded.Type.Exception -> MockNetworkDomainModel.Response.Exception(
            delay = this.delay,
            classPath = this.type.classPath,
        )
    }
}
