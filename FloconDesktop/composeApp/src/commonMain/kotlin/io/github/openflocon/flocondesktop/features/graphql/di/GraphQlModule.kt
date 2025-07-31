package io.github.openflocon.flocondesktop.features.graphql.di

import com.florent37.flocondesktop.features.graphql.data.di.graphQlDataModule
import com.florent37.flocondesktop.features.graphql.domain.di.graphQlDomainModule
import com.florent37.flocondesktop.features.graphql.ui.di.graphQlUiModule
import org.koin.dsl.module

val graphqlModule =
    module {
        includes(
            graphQlDataModule,
            graphQlDomainModule,
            graphQlUiModule,
        )
    }
