package io.github.openflocon.data.local.sharedpreference

import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesLocalDataSource
import io.github.openflocon.data.core.sharedpreference.datasource.DeviceSharedPreferencesValuesDataSource
import io.github.openflocon.data.local.sharedpreference.datasources.DeviceSharedPreferencesLocalDataSourceImpl
import io.github.openflocon.data.local.sharedpreference.datasources.DeviceSharedPreferencesValuesDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val sharedPreferenceModule = module {
    singleOf(::DeviceSharedPreferencesValuesDataSourceImpl) bind DeviceSharedPreferencesValuesDataSource::class
    singleOf(::DeviceSharedPreferencesLocalDataSourceImpl) bind DeviceSharedPreferencesLocalDataSource::class
}
