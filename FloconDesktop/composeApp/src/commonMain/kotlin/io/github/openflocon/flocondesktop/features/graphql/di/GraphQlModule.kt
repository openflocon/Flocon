package io.github.openflocon.flocondesktop.features.graphql.di

import io.github.openflocon.flocondesktop.features.graphql.data.di.graphQlDataModule
import io.github.openflocon.flocondesktop.features.graphql.domain.di.graphQlDomainModule
import io.github.openflocon.flocondesktop.features.graphql.ui.di.graphQlUiModule
import org.koin.dsl.module

val graphqlModule =
    module {
        includes(
            graphQlDataModule,
            graphQlDomainModule,
            graphQlUiModule,
        )
    }
