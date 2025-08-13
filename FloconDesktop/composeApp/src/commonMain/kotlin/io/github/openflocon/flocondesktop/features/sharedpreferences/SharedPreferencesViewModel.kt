package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.usecase.EditSharedPrefFieldUseCase
import io.github.openflocon.domain.sharedpreference.usecase.ObserveCurrentDeviceSharedPreferenceValuesUseCase
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.sharedpreferences.delegate.SharedPrefSelectorDelegate
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedPreferencesViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val sharedPrefSelectorDelegate: SharedPrefSelectorDelegate,
    private val observeCurrentDeviceSharedPreferenceValuesUseCase: ObserveCurrentDeviceSharedPreferenceValuesUseCase,
    private val editSharedPrefFieldUseCase: EditSharedPrefFieldUseCase,
) : ViewModel() {
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
                SharingStarted.Companion.WhileSubscribed(5_000),
                SharedPreferencesRowsStateUiModel.Loading,
            )

    fun onVisible() {
        sharedPrefSelectorDelegate.start()
    }

    fun onNotVisible() {
        sharedPrefSelectorDelegate.stop()
    }

    fun onSharedPrefsSelected(selected: DeviceSharedPrefUiModel) {
        sharedPrefSelectorDelegate.onSharedPreferenceSelected(selected)
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
}
