package io.github.openflocon.flocondesktop.features.graphql.domain.di

import io.github.openflocon.flocondesktop.features.graphql.domain.DeleteGraphQlRequestUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.DeleteGraphQlRequestsBeforeUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ObserveGraphQlRequestsByIdUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ObserveGraphQlRequestsUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.ResetCurrentDeviceGraphQlRequestsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val graphQlDomainModule =
    module {
        factoryOf(::ObserveGraphQlRequestsUseCase)
        factoryOf(::ObserveGraphQlRequestsByIdUseCase)
        factoryOf(::DeleteGraphQlRequestUseCase)
        factoryOf(::DeleteGraphQlRequestsBeforeUseCase)
        factoryOf(::ResetCurrentDeviceGraphQlRequestsUseCase)
    }
