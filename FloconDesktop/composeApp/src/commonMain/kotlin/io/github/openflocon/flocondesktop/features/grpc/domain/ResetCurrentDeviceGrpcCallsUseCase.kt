package io.github.openflocon.flocondesktop.features.grpc.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.repository.GRPCRepository

class ResetCurrentDeviceGrpcCallsUseCase(
    private val grpcRepository: GRPCRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        grpcRepository.deleteCallsForDevice(deviceId = deviceId)
    }
}
