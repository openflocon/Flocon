package io.github.openflocon.domain.sharedpreference

import io.github.openflocon.domain.sharedpreference.usecase.AskForDeviceSharedPreferencesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.EditSharedPrefFieldUseCase
import io.github.openflocon.domain.sharedpreference.usecase.GetCurrentDeviceSelectedSharedPreferenceUseCase
import io.github.openflocon.domain.sharedpreference.usecase.GetCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSelectedSharedPreferenceUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveDeviceSharedPreferencesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.SelectCurrentDeviceSharedPreferenceUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val sharedPreferencesModule = module {
    factoryOf(::AskForDeviceSharedPreferencesUseCase)
    factoryOf(::GetCurrentDeviceSelectedSharedPreferenceUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedSharedPreferenceUseCase)
    factoryOf(::ObserveDeviceSharedPreferencesUseCase)
    factoryOf(::SelectCurrentDeviceSharedPreferenceUseCase)
    factoryOf(::GetCurrentDeviceSharedPreferenceValuesUseCase)
    factoryOf(::ObserveCurrentDeviceSharedPreferenceValuesUseCase)
    factoryOf(::EditSharedPrefFieldUseCase)
}
