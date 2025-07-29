package com.florent37.flocondesktop.features.sharedpreferences.data.di

import com.florent37.flocondesktop.features.sharedpreferences.data.SharedPreferencesRepositoryImpl
import com.florent37.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesDataSource
import com.florent37.flocondesktop.features.sharedpreferences.data.datasources.DeviceSharedPreferencesValuesDataSource
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
