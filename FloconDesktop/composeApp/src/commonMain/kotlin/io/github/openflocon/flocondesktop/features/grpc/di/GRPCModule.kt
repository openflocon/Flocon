package io.github.openflocon.flocondesktop.features.grpc.di

import com.florent37.flocondesktop.features.grpc.data.di.grpcDataModule
import com.florent37.flocondesktop.features.grpc.domain.di.grpcDomainModule
import com.florent37.flocondesktop.features.grpc.ui.di.grpcUiModule
import org.koin.dsl.module

val grpcModule =
    module {
        includes(
            grpcDataModule,
            grpcDomainModule,
            grpcUiModule,
        )
    }
