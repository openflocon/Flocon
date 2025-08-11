package io.github.openflocon.flocondesktop.features.analytics.data.di

import io.github.openflocon.flocondesktop.features.analytics.data.AnalyticsRepositoryImpl
import com.flocon.data.remote.analytics.datasource.AnalyticsRemoteDataSourceImpl
import io.github.openflocon.data.core.analytics.datasource.DeviceAnalyticsDataSource
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.device.DeviceAnalyticsDataSourceInMemory
import io.github.openflocon.data.core.analytics.datasource.AnalyticsLocalDataSource
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
