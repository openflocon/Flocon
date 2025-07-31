package io.github.openflocon.flocondesktop.features.graphql.data.di

import io.github.openflocon.flocondesktop.features.graphql.data.GraphQlRepositoryImpl
import io.github.openflocon.flocondesktop.features.graphql.data.datasource.LocalGraphQlDataSource
import io.github.openflocon.flocondesktop.features.graphql.data.datasource.room.LocalGraphQlDataSourceImpl
import io.github.openflocon.flocondesktop.features.graphql.domain.repository.GraphQlRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val graphQlDataModule =
    module {
        factoryOf(::GraphQlRepositoryImpl) {
            bind<GraphQlRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::LocalGraphQlDataSourceImpl) {
            bind<LocalGraphQlDataSource>()
        }
    }
