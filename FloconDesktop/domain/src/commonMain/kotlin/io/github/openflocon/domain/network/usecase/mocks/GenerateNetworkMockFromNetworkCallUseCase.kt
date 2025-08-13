package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.usecase.ObserveHttpRequestsByIdUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GenerateNetworkMockFromNetworkCallUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val observeHttpRequestsByIdUseCase: ObserveHttpRequestsByIdUseCase,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        requestId: String,
    ): MockNetworkDomainModel? {
        return getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            observeHttpRequestsByIdUseCase(requestId = requestId).firstOrNull()
        }?.let { request ->
            MockNetworkDomainModel(
                id = Uuid.Companion.random().toString(), // generate
                expectation = MockNetworkDomainModel.Expectation(
                    urlPattern = request.networkRequest.url,
                    method = request.networkRequest.method,
                ),
                isEnabled = true, // enabled by default
                response = request.networkResponse?.let {
                    MockNetworkDomainModel.Response(
                        httpCode = request.httpCode() ?: 200,
                        body = it.body ?: "",
                        mediaType = it.headers["Content-Type"] ?: "",
                        delay = 0,
                        headers = it.headers,
                    )
                } ?: MockNetworkDomainModel.Response(
                    httpCode = 200,
                    body = "",
                    mediaType = "application/json",
                    delay = 0,
                    headers = emptyMap(),
                )
            )
        }
    }
}
