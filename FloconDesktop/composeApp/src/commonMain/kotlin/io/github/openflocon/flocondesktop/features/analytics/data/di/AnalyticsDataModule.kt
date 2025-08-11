package io.github.openflocon.flocondesktop.features.analytics.data.di

import com.flocon.data.remote.analytics.datasource.AnalyticsRemoteDataSourceImpl
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.flocondesktop.features.analytics.data.AnalyticsRepositoryImpl
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val analyticsDataModule =
    module {
        singleOf(::AnalyticsRepositoryImpl) {
            bind<AnalyticsRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::AnalyticsRemoteDataSourceImpl)
    }
