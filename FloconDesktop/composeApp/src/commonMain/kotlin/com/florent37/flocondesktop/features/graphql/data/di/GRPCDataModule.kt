package com.florent37.flocondesktop.features.graphql.data.di

import com.florent37.flocondesktop.features.graphql.data.GraphQlRepositoryImpl
import com.florent37.flocondesktop.features.graphql.data.datasource.LocalGraphQlDataSource
import com.florent37.flocondesktop.features.graphql.data.datasource.room.LocalGraphQlDataSourceImpl
import com.florent37.flocondesktop.features.graphql.domain.repository.GraphQlRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
