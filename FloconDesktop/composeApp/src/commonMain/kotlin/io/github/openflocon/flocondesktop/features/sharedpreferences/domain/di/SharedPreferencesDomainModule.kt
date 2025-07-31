package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.di

import com.florent37.flocondesktop.features.sharedpreferences.domain.AskForDeviceSharedPreferencesUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.EditSharedPrefFieldUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.GetCurrentDeviceSelectedSharedPreferenceUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.GetCurrentDeviceSharedPreferenceValuesUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.ObserveCurrentDeviceSelectedSharedPreferenceUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.ObserveDeviceSharedPreferencesUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.SelectCurrentDeviceSharedPreferenceUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sharedPreferencesDomainModule =
    module {
        factoryOf(::AskForDeviceSharedPreferencesUseCase)
        factoryOf(::GetCurrentDeviceSelectedSharedPreferenceUseCase)
        factoryOf(::ObserveCurrentDeviceSelectedSharedPreferenceUseCase)
        factoryOf(::ObserveDeviceSharedPreferencesUseCase)
        factoryOf(::SelectCurrentDeviceSharedPreferenceUseCase)
        factoryOf(::GetCurrentDeviceSharedPreferenceValuesUseCase)
        factoryOf(::ObserveCurrentDeviceSharedPreferenceValuesUseCase)
        factoryOf(::EditSharedPrefFieldUseCase)
    }
