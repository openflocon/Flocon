package io.github.openflocon.data.core.analytics

import io.github.openflocon.data.core.analytics.repository.AnalyticsRepositoryImpl
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val analyticsModule = module {
    singleOf(::AnalyticsRepositoryImpl) {
        bind<AnalyticsRepository>()
        bind<MessagesReceiverRepository>()
    }
}
