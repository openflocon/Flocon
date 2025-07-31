package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.di

import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.AskForDeviceSharedPreferencesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.EditSharedPrefFieldUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.GetCurrentDeviceSelectedSharedPreferenceUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.GetCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.ObserveCurrentDeviceSelectedSharedPreferenceUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.ObserveDeviceSharedPreferencesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.SelectCurrentDeviceSharedPreferenceUseCase
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
