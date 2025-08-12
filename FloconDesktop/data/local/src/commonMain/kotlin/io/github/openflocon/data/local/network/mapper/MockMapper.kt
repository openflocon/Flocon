package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.mock.MockNetworkExpectationEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEmbedded
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.MockNetworkResponseDomainModel


fun toEntity(
    domainModel: MockNetworkResponseDomainModel,
    deviceInfo: DeviceIdAndPackageNameDomainModel
): MockNetworkResponseEntity {
    return MockNetworkResponseEntity(
        deviceId = deviceInfo.deviceId,
        packageName = deviceInfo.packageName,
        mockId = domainModel.id,
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

fun toDomain(entity: MockNetworkResponseEntity): MockNetworkResponseDomainModel {
    return MockNetworkResponseDomainModel(
        id = entity.mockId,
        expectation = MockNetworkResponseDomainModel.Expectation(
            urlPattern = entity.expectation.urlPattern,
            method = entity.expectation.method,
        ),
        response = MockNetworkResponseDomainModel.Response(
            httpCode = entity.response.httpCode,
            body = entity.response.body,
            mediaType = entity.response.mediaType,
            delay = entity.response.delay,
            headers = entity.response.headers,
        )
    )
}
