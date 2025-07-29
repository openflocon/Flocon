package com.florent37.flocondesktop.features.grpc.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.repository.GRPCRepository

class DeleteGrpcCallBeforeUseCase(
    private val grpcRepository: GRPCRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(callId: GrpcCallId) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        grpcRepository.deleteCallsBefore(deviceId = deviceId, callId = callId)
    }
}
