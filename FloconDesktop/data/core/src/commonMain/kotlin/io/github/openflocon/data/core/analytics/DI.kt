package io.github.openflocon.data.core.analytics

import io.github.openflocon.data.core.analytics.repository.AnalyticsRepositoryImpl
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val analyticsModule = module {
    singleOf(::AnalyticsRepositoryImpl) bind AnalyticsRepository::class
}
