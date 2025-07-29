package com.florent37.flocondesktop.features.grpc.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import com.florent37.flocondesktop.features.grpc.domain.repository.GRPCRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveGrpcCallsUseCase(
    private val grpcRepository: GRPCRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<GrpcCallDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { currentDeviceId ->
        if (currentDeviceId == null) {
            flowOf(emptyList())
        } else {
            grpcRepository.observeCalls(currentDeviceId)
        }
    }
}
