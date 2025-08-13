package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.mock.MockNetworkExpectationEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel


fun toEntity(
    domainModel: MockNetworkDomainModel,
    deviceInfo: DeviceIdAndPackageNameDomainModel
): MockNetworkEntity {
    return MockNetworkEntity(
        deviceId = deviceInfo.deviceId,
        packageName = deviceInfo.packageName,
        mockId = domainModel.id,
        isEnabled = domainModel.isEnabled,
        expectation = MockNetworkExpectationEmbedded(
            urlPattern = domainModel.expectation.urlPattern,
            method = domainModel.expectation.method,
        ),
        response = MockNetworkResponseEmbedded(
            httpCode = domainModel.response.httpCode,
            body = domainModel.response.body,
            mediaType = domainModel.response.mediaType,
            delay = domainModel.response.delay,
            headers = domainModel.response.headers,
        )
    )
}

fun toDomain(entity: MockNetworkEntity): MockNetworkDomainModel {
    return MockNetworkDomainModel(
        id = entity.mockId,
        isEnabled = entity.isEnabled,
        expectation = MockNetworkDomainModel.Expectation(
            urlPattern = entity.expectation.urlPattern,
            method = entity.expectation.method,
        ),
        response = MockNetworkDomainModel.Response(
            httpCode = entity.response.httpCode,
            body = entity.response.body,
            mediaType = entity.response.mediaType,
            delay = entity.response.delay,
            headers = entity.response.headers,
        )
    )
}
