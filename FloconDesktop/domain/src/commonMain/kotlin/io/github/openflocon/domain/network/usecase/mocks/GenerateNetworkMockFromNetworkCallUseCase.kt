package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.usecase.ObserveNetworkRequestsByIdUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GenerateNetworkMockFromNetworkCallUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val observeNetworkRequestsByIdUseCase: ObserveNetworkRequestsByIdUseCase,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        requestId: String,
    ): MockNetworkDomainModel? = getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
        observeNetworkRequestsByIdUseCase(requestId = requestId).firstOrNull()
    }?.let { request ->
        MockNetworkDomainModel(
            id = Uuid.random().toString(), // generate
            expectation = MockNetworkDomainModel.Expectation(
                urlPattern = Regex.escape(request.request.url),
                method = request.request.method,
            ),
            isEnabled = true, // enabled by default
            isShared = false,
            response = request.response?.let {
                when (it) {
                    is FloconNetworkCallDomainModel.Response.Failure -> null // maybe generate error response in this case
                    is FloconNetworkCallDomainModel.Response.Success -> MockNetworkDomainModel.Response.Body(
                        httpCode = request.httpCode() ?: 200,
                        body = it.body ?: "",
                        mediaType = it.headers["Content-Type"] ?: "",
                        delay = 0,
                        headers = it.headers,
                    )
                }
            } ?: MockNetworkDomainModel.Response.Body(
                httpCode = 200,
                body = "",
                mediaType = "application/json",
                delay = 0,
                headers = emptyMap(),
            )
        )
    }
}
