package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class SendNetworkWebsocketMockUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
) {
    suspend operator fun invoke(
        websocketId: NetworkWebsocketId,
        message: String,
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.sendWebsocketMock(
                deviceIdAndPackageName = deviceIdAndPackageName,
                websocketId = websocketId,
                message = message,
            )
        }
    }
}
