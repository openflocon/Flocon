package io.github.openflocon.flocondesktop.features.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.usecase.DeleteAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.InsertOrUpdateAdbCommandUseCase
import io.github.openflocon.domain.commands.usecase.ObserveAdbCommandsUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

internal class AdbCommandViewModel(
    private val observeAdbCommandsUseCase: ObserveAdbCommandsUseCase,
    private val insertOrUpdateAdbCommandUseCase: InsertOrUpdateAdbCommandUseCase,
    private val deleteAdbCommandUseCase: DeleteAdbCommandUseCase,
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val currentDeviceUseCase: GetCurrentDeviceIdAndPackageNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdbUiState(persistentListOf()))

    val uiState = observeAdbCommandsUseCase()
        .mapLatest { list -> list.map(AdbCommand::toUi) }
        .mapLatest { AdbUiState(commands = it.toImmutableList()) }
        .stateInWhileSubscribed(AdbUiState(persistentListOf()))

    fun onAction(action: AdbCommandAction) {
        when (action) {
            is AdbCommandAction.Create -> onCreate(action)
            is AdbCommandAction.Execute -> onExecute(action)
        }
    }

    private fun onCreate(action: AdbCommandAction.Create) {
        viewModelScope.launch {
            insertOrUpdateAdbCommandUseCase(
                command = AdbCommand(id = 0, command = action.command)
            )
        }
    }

    private fun onExecute(action: AdbCommandAction.Execute) {
        viewModelScope.launch {
            val currentDevice = currentDeviceUseCase()

            executeAdbCommandUseCase(
                target = AdbCommandTargetDomainModel.Device(
                    deviceId = currentDevice?.deviceId
                        ?: return@launch
                ),
                action.command.command
            )
        }
    }

}
