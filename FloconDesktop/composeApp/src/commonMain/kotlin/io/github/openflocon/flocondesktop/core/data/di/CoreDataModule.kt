package io.github.openflocon.flocondesktop.core.data.di

import com.florent37.flocondesktop.core.data.device.DevicesRepositoryImpl
import com.florent37.flocondesktop.core.data.settings.SettingsRepositoryImpl
import com.florent37.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import com.florent37.flocondesktop.core.data.settings.datasource.local.SettingsDataSourcePrefs
import com.florent37.flocondesktop.core.domain.device.repository.DevicesRepository
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDataModule =
    module {
        factoryOf(::SettingsRepositoryImpl) {
            bind<SettingsRepository>()
        }
        singleOf(::SettingsDataSourcePrefs) {
            bind<SettingsDataSource>()
        }
        singleOf(::DevicesRepositoryImpl) {
            bind<DevicesRepository>()
        }
    }
