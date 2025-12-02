package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.usecase.EditSharedPrefFieldUseCase
import io.github.openflocon.domain.sharedpreference.usecase.GetCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.delegate.SharedPrefSelectorDelegate
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferenceToEdit
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.PreferenceAutoUpdate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class SharedPreferencesViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val sharedPrefSelectorDelegate: SharedPrefSelectorDelegate,
    private val observeCurrentDeviceSharedPreferenceValuesUseCase: ObserveCurrentDeviceSharedPreferenceValuesUseCase,
    private val editSharedPrefFieldUseCase: EditSharedPrefFieldUseCase,
    private val getCurrentDeviceSharedPrefValuesUseCase: GetCurrentDeviceSharedPreferenceValuesUseCase,
) : ViewModel() {

    private val _elementToEdit = MutableStateFlow<SharedPreferenceToEdit?>(null)
    val elementToEdit = _elementToEdit.asStateFlow()

    val sharedPrefs: StateFlow<SharedPrefsStateUiModel> =
        sharedPrefSelectorDelegate.deviceSharedPrefs
    val rows: StateFlow<SharedPreferencesRowsStateUiModel> =
        observeCurrentDeviceSharedPreferenceValuesUseCase()
            .map {
                if (it.isEmpty()) {
                    SharedPreferencesRowsStateUiModel.Empty
                } else {
                    SharedPreferencesRowsStateUiModel.WithContent(
                        it.map {
                            SharedPreferencesRowUiModel(
                                key = it.key,
                                value = it.value.toUi(),
                            )
                        },
                    )
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                SharedPreferencesRowsStateUiModel.Loading,
            )

    data class PreferenceAutoUpdateState(
        val isEnabled: Boolean,
        val delayMs: Int,
    ) {
        fun toUi(): PreferenceAutoUpdate = if (isEnabled) {
            PreferenceAutoUpdate.Enabled(
                delayMs = delayMs
            )
        } else {
            PreferenceAutoUpdate.Disabled
        }
    }

    private val autoUpdate = MutableStateFlow<PreferenceAutoUpdateState>(
        PreferenceAutoUpdateState(
            isEnabled = false,
            delayMs = 3000,
        )
    )
    val autoUpdateState: StateFlow<PreferenceAutoUpdate> = autoUpdate
        .map { it.toUi() }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            autoUpdate.value.toUi()
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            autoUpdate.collectLatest {
                if (it.isEnabled) {
                    while (true) {
                        getCurrentDeviceSharedPrefValuesUseCase()
                        delay(max(it.delayMs.toLong(), 300L))
                    }
                }
            }
        }
    }

    fun onVisible() {
        sharedPrefSelectorDelegate.start()
    }

    fun onNotVisible() {
        sharedPrefSelectorDelegate.stop()
    }

    fun onSharedPrefsSelected(selected: DeviceSharedPrefUiModel) {
        sharedPrefSelectorDelegate.onSharedPreferenceSelected(selected)
    }

    fun refreshClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            getCurrentDeviceSharedPrefValuesUseCase()
        }
    }

    fun onAutoUpdateChange(enabled: Boolean) {
        autoUpdate.update {
            it.copy(isEnabled = enabled)
        }
    }

    fun onAutoUpdateDelayChanged(delayMs: Int) {
        autoUpdate.update {
            it.copy(delayMs = delayMs)
        }
    }

    fun onValueChanged(item: SharedPreferencesRowUiModel, valueAsString: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (item.value) {
                is SharedPreferencesRowUiModel.Value.StringValue -> {
                    val stringValue =
                        SharedPreferenceRowDomainModel.Value.StringValue(valueAsString)
                    editSharedPrefFieldUseCase(item.key, stringValue)
                }

                is SharedPreferencesRowUiModel.Value.IntValue -> {
                    val intValue = try {
                        SharedPreferenceRowDomainModel.Value.IntValue(valueAsString.toInt())
                    } catch (t: Throwable) {
                        feedbackDisplayer.displayMessage("The value is not an integer")
                        return@launch
                    }
                    editSharedPrefFieldUseCase(item.key, intValue)
                }

                is SharedPreferencesRowUiModel.Value.FloatValue -> {
                    val floatValue = try {
                        SharedPreferenceRowDomainModel.Value.FloatValue(valueAsString.toFloat())
                    } catch (t: Throwable) {
                        feedbackDisplayer.displayMessage("The value is not an float")
                        return@launch
                    }
                    editSharedPrefFieldUseCase(item.key, floatValue)
                }

                is SharedPreferencesRowUiModel.Value.LongValue -> {
                    val longValue = try {
                        SharedPreferenceRowDomainModel.Value.LongValue(valueAsString.toLong())
                    } catch (t: Throwable) {
                        feedbackDisplayer.displayMessage("The value is not an long")
                        return@launch
                    }
                    editSharedPrefFieldUseCase(item.key, longValue)
                }

                is SharedPreferencesRowUiModel.Value.BooleanValue -> {
                    val booleanValue = try {
                        SharedPreferenceRowDomainModel.Value.BooleanValue(valueAsString.toBoolean())
                    } catch (t: Throwable) {
                        feedbackDisplayer.displayMessage("The value is not a boolean")
                        return@launch
                    }
                    editSharedPrefFieldUseCase(item.key, booleanValue)
                }

                is SharedPreferencesRowUiModel.Value.StringSetValue -> {
                    // no editable
                }
            }
        }
    }

    fun onEditClicked(
        row: SharedPreferencesRowUiModel,
        stringValue: SharedPreferencesRowUiModel.Value.StringValue
    ) {
        _elementToEdit.update { SharedPreferenceToEdit(row, stringValue) }
    }

    fun cancelEdition() {
        _elementToEdit.update { null }
    }

    fun onEditDone(row: SharedPreferencesRowUiModel, stringValue: String) {
        onValueChanged(row, stringValue)
        _elementToEdit.update { null }
    }
}
