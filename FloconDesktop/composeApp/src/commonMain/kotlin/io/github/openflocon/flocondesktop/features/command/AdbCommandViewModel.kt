package io.github.openflocon.flocondesktop.features.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.usecase.DeleteAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.GetAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.InsertOrUpdateAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.ObserveAdbCommandsUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentIsActiveUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdbCommandViewModel(
    private val observeAdbCommandsUseCase: ObserveAdbCommandsUseCase,
    private val insertOrUpdateAdbCommandUseCase: InsertOrUpdateAdbCommandUseCase,
    private val deleteAdbCommandUseCase: DeleteAdbCommandUseCase,
    private val getAdbCommandUseCase: GetAdbCommandUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val currentDeviceUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val observeIsCurrentActiveDevicesUseCase: ObserveIsCurrentIsActiveUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    private val _history = MutableStateFlow(persistentListOf<AdbCommandUi>())

    val uiState = combine(
        observeAdbCommandsUseCase().mapLatest { list ->
            list.map(AdbCommand::toUi)
        },
        _history
    ) { commands, history ->
        AdbUiState(commands = commands.toImmutableList(), history = history)
    }
        .stateInWhileSubscribed(AdbUiState(persistentListOf()))

    fun onAction(action: AdbCommandAction) {
        when (action) {
            is AdbCommandAction.Create -> onCreate(action)
            is AdbCommandAction.Execute -> onExecute(action)
            is AdbCommandAction.Delete -> onDelete(action)
        }
    }

    private fun onCreate(action: AdbCommandAction.Create) {
        viewModelScope.launch {
            insertOrUpdateAdbCommandUseCase(command = AdbCommand(id = 0, command = action.command))
        }
    }

    private fun onDelete(action: AdbCommandAction.Delete) {
        viewModelScope.launch {
            val command = getAdbCommandUseCase(action.command.id)
            if (command != null) {
                deleteAdbCommandUseCase(command)
            }
        }
    }

    private fun onExecute(action: AdbCommandAction.Execute) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDevice = currentDeviceUseCase()
            val isConnected = observeIsCurrentActiveDevicesUseCase().firstOrNull()
            if ((isConnected == false) or (currentDevice == null)) {
                feedbackDisplayer.displayMessage(
                    message = "No device selected or connected",
                    type = FeedbackDisplayer.MessageType.Error
                )
                return@launch
            }

            val historyItem = action.command.copy(loading = true)
            _history.update { it.add(0, historyItem) }

            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(
                    deviceId = currentDevice?.deviceId ?: return@launch
                ),
                command = action.command.command
            )
                .alsoSuccess { output -> updateHistoryItem(historyItem, output) }
                .alsoFailure { error ->
                    updateHistoryItem(historyItem, "Error: ${error.message}")
                }
        }
    }

    private fun updateHistoryItem(originalItem: AdbCommandUi, output: String) {
        _history.update { history ->
            history.map {
                if (it == originalItem) it.copy(loading = false, output = output) else it
            }
                .toPersistentList()
        }
    }

    fun onVisible() = Unit

    fun onNotVisible() = Unit
}
