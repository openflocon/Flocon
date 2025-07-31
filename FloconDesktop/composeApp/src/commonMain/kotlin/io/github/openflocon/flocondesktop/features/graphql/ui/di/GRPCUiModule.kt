package io.github.openflocon.flocondesktop.features.graphql.ui.di

import com.florent37.flocondesktop.features.graphql.ui.GraphQlViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val graphQlUiModule =
    module {
        viewModelOf(::GraphQlViewModel)
    }
