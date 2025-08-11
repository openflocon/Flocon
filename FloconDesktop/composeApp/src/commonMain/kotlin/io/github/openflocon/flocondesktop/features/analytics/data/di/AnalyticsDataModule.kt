package io.github.openflocon.flocondesktop.features.analytics.data.di

import io.github.openflocon.flocondesktop.features.analytics.data.AnalyticsRepositoryImpl
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.AnalyticsRemoteDataSourceImpl
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSourceInMemory
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSourceRoom
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
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
        singleOf(::AnalyticsLocalDataSourceRoom) {
            bind<AnalyticsLocalDataSource>()
        }
        singleOf(::DeviceAnalyticsDataSourceInMemory) {
            bind<DeviceAnalyticsDataSource>()
        }
        singleOf(::AnalyticsRemoteDataSourceImpl)
    }
