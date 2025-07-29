package com.florent37.flocondesktop.features.grpc.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.grpc.domain.repository.GRPCRepository

class ResetCurrentDeviceGrpcCallsUseCase(
    private val grpcRepository: GRPCRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        grpcRepository.deleteCallsForDevice(deviceId = deviceId)
    }
}
