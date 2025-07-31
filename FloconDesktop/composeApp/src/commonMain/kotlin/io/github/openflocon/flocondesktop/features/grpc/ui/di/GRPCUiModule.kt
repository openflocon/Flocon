package io.github.openflocon.flocondesktop.features.grpc.ui.di

import com.florent37.flocondesktop.features.grpc.ui.GRPCViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val grpcUiModule =
    module {
        viewModelOf(::GRPCViewModel)
    }
