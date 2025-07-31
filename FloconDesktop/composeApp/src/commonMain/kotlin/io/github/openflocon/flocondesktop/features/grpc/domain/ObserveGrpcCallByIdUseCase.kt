package io.github.openflocon.flocondesktop.features.grpc.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.repository.GRPCRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveGrpcCallByIdUseCase(
    private val grpcRepository: GRPCRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(callId: GrpcCallId): Flow<GrpcCallDomainModel?> = observeCurrentDeviceIdUseCase().flatMapLatest { currentDeviceId ->
        if (currentDeviceId == null) {
            flowOf(null)
        } else {
            grpcRepository.observeCall(currentDeviceId, callId = callId)
        }
    }
}
