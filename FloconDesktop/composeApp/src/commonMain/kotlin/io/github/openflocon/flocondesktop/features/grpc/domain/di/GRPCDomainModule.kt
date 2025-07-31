package io.github.openflocon.flocondesktop.features.grpc.domain.di

import io.github.openflocon.flocondesktop.features.grpc.domain.DeleteGrpcCallBeforeUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.DeleteGrpcCallUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ObserveGrpcCallByIdUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ObserveGrpcCallsUseCase
import io.github.openflocon.flocondesktop.features.grpc.domain.ResetCurrentDeviceGrpcCallsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val grpcDomainModule =
    module {
        factoryOf(::ObserveGrpcCallsUseCase)
        factoryOf(::ObserveGrpcCallByIdUseCase)
        factoryOf(::DeleteGrpcCallUseCase)
        factoryOf(::DeleteGrpcCallBeforeUseCase)
        factoryOf(::ResetCurrentDeviceGrpcCallsUseCase)
    }
