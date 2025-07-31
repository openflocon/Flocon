package io.github.openflocon.flocondesktop.features.grpc.di

import io.github.openflocon.flocondesktop.features.grpc.data.di.grpcDataModule
import io.github.openflocon.flocondesktop.features.grpc.domain.di.grpcDomainModule
import io.github.openflocon.flocondesktop.features.grpc.ui.di.grpcUiModule
import org.koin.dsl.module

val grpcModule =
    module {
        includes(
            grpcDataModule,
            grpcDomainModule,
            grpcUiModule,
        )
    }
