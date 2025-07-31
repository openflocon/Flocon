package io.github.openflocon.flocondesktop.features.grpc.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import io.github.openflocon.flocondesktop.features.grpc.domain.repository.GRPCRepository

class DeleteGrpcCallBeforeUseCase(
    private val grpcRepository: GRPCRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(callId: GrpcCallId) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        grpcRepository.deleteCallsBefore(deviceId = deviceId, callId = callId)
    }
}
