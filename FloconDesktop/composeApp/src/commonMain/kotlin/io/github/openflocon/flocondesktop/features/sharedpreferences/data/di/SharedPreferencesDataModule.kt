package io.github.openflocon.flocondesktop.features.sharedpreferences.data.di

import io.github.openflocon.flocondesktop.features.sharedpreferences.data.SharedPreferencesRepositoryImpl
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedPreferencesDataModule =
    module {
        factoryOf(::SharedPreferencesRepositoryImpl) {
            bind<SharedPreferencesRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::DeviceSharedPreferencesDataSource)
        singleOf(::DeviceSharedPreferencesValuesDataSource)
    }
