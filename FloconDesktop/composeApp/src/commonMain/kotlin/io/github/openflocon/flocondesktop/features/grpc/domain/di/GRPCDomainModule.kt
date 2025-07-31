package io.github.openflocon.flocondesktop.features.grpc.domain.di

import com.florent37.flocondesktop.features.grpc.domain.DeleteGrpcCallBeforeUseCase
import com.florent37.flocondesktop.features.grpc.domain.DeleteGrpcCallUseCase
import com.florent37.flocondesktop.features.grpc.domain.ObserveGrpcCallByIdUseCase
import com.florent37.flocondesktop.features.grpc.domain.ObserveGrpcCallsUseCase
import com.florent37.flocondesktop.features.grpc.domain.ResetCurrentDeviceGrpcCallsUseCase
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
