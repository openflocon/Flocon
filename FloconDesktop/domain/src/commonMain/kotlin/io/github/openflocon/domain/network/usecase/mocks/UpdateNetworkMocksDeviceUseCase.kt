package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.MockDeviceTarget
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class UpdateNetworkMocksDeviceUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        id: String,
        target: MockDeviceTarget,
    ): Either<Throwable, Unit> {
        val deviceIdAndPackageName = when (target) {
            MockDeviceTarget.AllDevicesAndApps -> null
            MockDeviceTarget.SpecificToCurrentDeviceAndApp -> {
                getCurrentDeviceIdAndPackageNameUseCase()
                    ?: return Failure(Throwable("No device selected"))
            }
        }

        networkMocksRepository.updateMockDevice(
            mockId = id,
            deviceIdAndPackageName = deviceIdAndPackageName,
        )
        // after a change, update the device mocks
        setupNetworkMocksUseCase()

        return Success(Unit)
    }
}
