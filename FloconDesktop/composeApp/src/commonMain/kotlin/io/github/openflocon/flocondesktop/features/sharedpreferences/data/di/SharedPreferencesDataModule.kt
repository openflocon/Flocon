package io.github.openflocon.flocondesktop.features.sharedpreferences.data.di

import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import io.github.openflocon.flocondesktop.features.sharedpreferences.data.SharedPreferencesRepositoryImpl
import io.github.openflocon.data.local.sharedpreference.datasources.DeviceSharedPreferencesDataSource
import io.github.openflocon.data.local.sharedpreference.datasources.DeviceSharedPreferencesValuesDataSourceImpl
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedPreferencesDataModule =
    module {
        factoryOf(::SharedPreferencesRepositoryImpl) {
            bind<SharedPreferencesRepository>()
            bind<MessagesReceiverRepository>()
        }
        singleOf(::DeviceSharedPreferencesDataSource)
        singleOf(::DeviceSharedPreferencesValuesDataSourceImpl) bind DeviceSharedPreferencesValuesDataSource::class
    }
