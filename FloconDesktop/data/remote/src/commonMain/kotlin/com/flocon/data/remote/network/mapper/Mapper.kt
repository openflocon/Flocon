package com.flocon.data.remote.network.mapper

import com.flocon.data.remote.network.models.MockNetworkResponseDataModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel

fun listToRemote(mocks: List<MockNetworkDomainModel>): List<MockNetworkResponseDataModel> = mocks.map { toRemote(it) }

fun toRemote(mock: MockNetworkDomainModel) : MockNetworkResponseDataModel {
    return MockNetworkResponseDataModel(
        expectation = MockNetworkResponseDataModel.Expectation(
            urlPattern = mock.expectation.urlPattern,
            method = mock.expectation.method,
        ),
        response = MockNetworkResponseDataModel.Response(
            httpCode = mock.response.httpCode,
            body = mock.response.body,
            mediaType = mock.response.mediaType,
            delay = mock.response.delay,
            headers = mock.response.headers,
        )
    )
}
