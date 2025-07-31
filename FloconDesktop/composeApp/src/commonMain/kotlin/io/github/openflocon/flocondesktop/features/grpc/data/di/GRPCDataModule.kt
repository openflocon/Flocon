package io.github.openflocon.flocondesktop.features.grpc.data.di

import io.github.openflocon.flocondesktop.features.grpc.data.GRPCRepositoryImpl
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.LocalGrpcDataSource
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.LocalGrpcDataSourceImpl
import io.github.openflocon.flocondesktop.features.grpc.domain.repository.GRPCRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val grpcDataModule =
    module {
        factoryOf(::GRPCRepositoryImpl) {
            bind<GRPCRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::LocalGrpcDataSourceImpl) {
            bind<LocalGrpcDataSource>()
        }
    }
