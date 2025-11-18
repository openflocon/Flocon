package io.github.openflocon.flocondesktop.features.sharedpreferences.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.usecase.AskForDeviceSharedPreferencesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.GetCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSelectedSharedPreferenceUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveDeviceSharedPreferencesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.SelectCurrentDeviceSharedPreferenceUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedPrefSelectorDelegate(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val observeDeviceSharedPrefUseCase: ObserveDeviceSharedPreferencesUseCase,
    private val observeCurrentDeviceSelectedSharedPrefUseCase: ObserveCurrentDeviceSelectedSharedPreferenceUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val askForDeviceSharedPrefsUseCase: AskForDeviceSharedPreferencesUseCase,
    private val selectCurrentDeviceSharedPrefUseCase: SelectCurrentDeviceSharedPreferenceUseCase,
    private val getCurrentDeviceSharedPrefValuesUseCase: GetCurrentDeviceSharedPreferenceValuesUseCase,
) : CloseableScoped by closeableDelegate {
    val deviceSharedPrefs: StateFlow<SharedPrefsStateUiModel> =
        combine(
            observeDeviceSharedPrefUseCase(),
            observeCurrentDeviceSelectedSharedPrefUseCase(),
        ) { sharedPrefs, selected ->
            if (sharedPrefs.isEmpty()) {
                SharedPrefsStateUiModel.Empty
            } else {
                SharedPrefsStateUiModel.WithContent(
                    preferences = sharedPrefs.map { toUi(it) },
                    selected =
                    toUi(
                        selected ?: run {
                            sharedPrefs.first().also {
                                selectCurrentDeviceSharedPrefUseCase(it.id)
                            }
                        },
                    ),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                SharedPrefsStateUiModel.Loading,
            )

    fun toUi(sharedPref: DeviceSharedPreferenceDomainModel) = DeviceSharedPrefUiModel(
        id = sharedPref.id,
        name = sharedPref.name,
    )

    fun onSharedPreferenceSelected(sharedPref: DeviceSharedPrefUiModel) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceSharedPrefUseCase(sharedPref.id)
        }
    }

    private var askForSharedPrefsJob: Job? = null
    private var listenSharedPreferenceValuesJob: Job? = null

    fun start() {
        askForSharedPrefsJob =
            coroutineScope.launch(dispatcherProvider.viewModel) {
                // if we change the device, we should ask again
                observeCurrentDeviceIdUseCase()
                    .distinctUntilChanged()
                    .collect {
                        askForDeviceSharedPrefsUseCase()
                    }
            }
        listenSharedPreferenceValuesJob =
            coroutineScope.launch(dispatcherProvider.viewModel) {
                // if we change the device, we should ask again
                observeCurrentDeviceSelectedSharedPrefUseCase()
                    .distinctUntilChanged()
                    .collect {
                        getCurrentDeviceSharedPrefValuesUseCase()
                    }
            }
    }

    fun stop() {
        askForSharedPrefsJob?.cancel()
        listenSharedPreferenceValuesJob?.cancel()
    }
}
