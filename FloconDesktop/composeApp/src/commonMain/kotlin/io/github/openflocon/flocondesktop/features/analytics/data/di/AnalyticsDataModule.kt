package io.github.openflocon.flocondesktop.features.analytics.data.di

import com.florent37.flocondesktop.features.analytics.data.AnalyticsRepositoryImpl
import com.florent37.flocondesktop.features.analytics.data.datasource.RemoteAnalyticsDataSource
import com.florent37.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSource
import com.florent37.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSourceInMemory
import com.florent37.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSource
import com.florent37.flocondesktop.features.analytics.data.datasource.local.AnalyticsLocalDataSourceRoom
import com.florent37.flocondesktop.features.analytics.domain.repository.AnalyticsRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
        singleOf(::RemoteAnalyticsDataSource)
    }
