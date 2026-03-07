package io.github.openflocon.flocondesktop.features.adbcommander

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowStepDomainModel
import io.github.openflocon.domain.adbcommander.usecase.ClearCommandHistoryUseCase
import io.github.openflocon.domain.adbcommander.usecase.DeleteFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.DeleteSavedCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.ExecuteAdbCommanderCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.ExecuteFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveCommandHistoryUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveFlowsUseCase
import io.github.openflocon.domain.adbcommander.usecase.ObserveSavedCommandsUseCase
import io.github.openflocon.domain.adbcommander.usecase.SaveCommandUseCase
import io.github.openflocon.domain.adbcommander.usecase.SaveFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.UpdateFlowUseCase
import io.github.openflocon.domain.adbcommander.usecase.UpdateSavedCommandUseCase
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.adbcommander.mapper.toUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderAction
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderTab
import io.github.openflocon.flocondesktop.features.adbcommander.model.AdbCommanderUiState
import io.github.openflocon.flocondesktop.features.adbcommander.model.ConsoleOutputEntry
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowEditorState
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowEditorStepState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdbCommanderViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val executeAdbCommanderCommandUseCase: ExecuteAdbCommanderCommandUseCase,
    private val observeSavedCommandsUseCase: ObserveSavedCommandsUseCase,
    private val saveCommandUseCase: SaveCommandUseCase,
    private val deleteSavedCommandUseCase: DeleteSavedCommandUseCase,
    private val updateSavedCommandUseCase: UpdateSavedCommandUseCase,
    private val observeCommandHistoryUseCase: ObserveCommandHistoryUseCase,
    private val clearCommandHistoryUseCase: ClearCommandHistoryUseCase,
    private val observeFlowsUseCase: ObserveFlowsUseCase,
    private val saveFlowUseCase: SaveFlowUseCase,
    private val deleteFlowUseCase: DeleteFlowUseCase,
    private val updateFlowUseCase: UpdateFlowUseCase,
    private val executeFlowUseCase: ExecuteFlowUseCase,
) : ViewModel() {

    private val localState = MutableStateFlow(AdbCommanderUiState())
    private var flowExecutionJob: Job? = null

    private val domainFlows: StateFlow<List<AdbFlowDomainModel>> = observeFlowsUseCase()
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val uiState: StateFlow<AdbCommanderUiState> = combine(
        localState,
        observeSavedCommandsUseCase().mapLatest { list -> list.map { it.toUiModel() } },
        observeCommandHistoryUseCase().mapLatest { list -> list.map { it.toUiModel() } },
        domainFlows.mapLatest { list -> list.map { it.toUiModel() } },
    ) { local, savedCommands, history, flows ->
        local.copy(
            savedCommands = savedCommands,
            history = history,
            flows = flows,
        )
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AdbCommanderUiState(),
        )

    fun onAction(action: AdbCommanderAction) {
        when (action) {
            is AdbCommanderAction.TabSelected -> onTabSelected(action.tab)
            is AdbCommanderAction.CommandInputChanged -> onCommandInputChanged(action.input)
            is AdbCommanderAction.ExecuteCommand -> onExecuteCommand()
            is AdbCommanderAction.SaveCurrentCommand -> onSaveCurrentCommand()
            is AdbCommanderAction.RunSavedCommand -> onRunSavedCommand(action.command)
            is AdbCommanderAction.DeleteSavedCommand -> onDeleteSavedCommand(action.id)
            is AdbCommanderAction.SaveQuickCommand -> onSaveQuickCommand(action.name, action.command)
            is AdbCommanderAction.ClearHistory -> onClearHistory()
            is AdbCommanderAction.RerunCommand -> onRerunCommand(action.command)
            is AdbCommanderAction.ClearConsole -> onClearConsole()
            is AdbCommanderAction.ShowFlowEditor -> onShowFlowEditor(action.flowId)
            is AdbCommanderAction.DismissFlowEditor -> onDismissFlowEditor()
            is AdbCommanderAction.FlowEditorNameChanged -> onFlowEditorNameChanged(action.name)
            is AdbCommanderAction.FlowEditorDescriptionChanged -> onFlowEditorDescriptionChanged(action.description)
            is AdbCommanderAction.FlowEditorStepCommandChanged -> onFlowEditorStepCommandChanged(action.index, action.command)
            is AdbCommanderAction.FlowEditorStepLabelChanged -> onFlowEditorStepLabelChanged(action.index, action.label)
            is AdbCommanderAction.FlowEditorStepDelayChanged -> onFlowEditorStepDelayChanged(action.index, action.delay)
            is AdbCommanderAction.FlowEditorAddStep -> onFlowEditorAddStep()
            is AdbCommanderAction.FlowEditorRemoveStep -> onFlowEditorRemoveStep(action.index)
            is AdbCommanderAction.SaveFlow -> onSaveFlow()
            is AdbCommanderAction.DeleteFlow -> onDeleteFlow(action.id)
            is AdbCommanderAction.ExecuteFlow -> onExecuteFlow(action.flowId)
            is AdbCommanderAction.CancelFlowExecution -> onCancelFlowExecution()
        }
    }

    private fun onTabSelected(tab: AdbCommanderTab) {
        localState.update { it.copy(selectedTab = tab) }
    }

    private fun onCommandInputChanged(input: String) {
        localState.update { it.copy(commandInput = input) }
    }

    private fun onExecuteCommand() {
        val command = localState.value.commandInput.trim()
        if (command.isEmpty()) return

        localState.update { it.copy(isExecuting = true) }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val result = executeAdbCommanderCommandUseCase(command)
            result.fold(
                doOnFailure = { error ->
                    localState.update {
                        it.copy(
                            isExecuting = false,
                            consoleOutput = it.consoleOutput + ConsoleOutputEntry(
                                command = command,
                                output = error.message ?: "Unknown error",
                                isSuccess = false,
                            ),
                            selectedTab = AdbCommanderTab.Runner,
                        )
                    }
                },
                doOnSuccess = { output ->
                    localState.update {
                        it.copy(
                            isExecuting = false,
                            commandInput = "",
                            consoleOutput = it.consoleOutput + ConsoleOutputEntry(
                                command = command,
                                output = output.ifEmpty { "(no output)" },
                                isSuccess = true,
                            ),
                            selectedTab = AdbCommanderTab.Runner,
                        )
                    }
                },
            )
        }
    }

    private fun onRunSavedCommand(command: String) {
        localState.update { it.copy(commandInput = command) }
        onExecuteCommand()
    }

    private fun onSaveCurrentCommand() {
        val command = localState.value.commandInput.trim()
        if (command.isEmpty()) {
            feedbackDisplayer.displayMessage(
                "Please enter a command first",
                type = FeedbackDisplayer.MessageType.Error,
            )
            return
        }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveCommandUseCase(
                AdbCommandDomainModel(
                    id = 0,
                    name = command,
                    command = command,
                    description = null,
                )
            )
            feedbackDisplayer.displayMessage("Command saved")
        }
    }

    private fun onSaveQuickCommand(name: String, command: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveCommandUseCase(
                AdbCommandDomainModel(
                    id = 0,
                    name = name,
                    command = command,
                    description = null,
                )
            )
            feedbackDisplayer.displayMessage("Command saved to library")
        }
    }

    private fun onDeleteSavedCommand(id: Long) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteSavedCommandUseCase(id)
            feedbackDisplayer.displayMessage("Command deleted")
        }
    }

    private fun onClearHistory() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            clearCommandHistoryUseCase()
            feedbackDisplayer.displayMessage("History cleared")
        }
    }

    private fun onRerunCommand(command: String) {
        localState.update { it.copy(commandInput = command) }
        onExecuteCommand()
    }

    private fun onShowFlowEditor(flowId: Long? = null) {
        if (flowId != null) {
            val flow = domainFlows.value.find { it.id == flowId }
            localState.update {
                it.copy(
                    showFlowEditor = true,
                    flowEditorState = FlowEditorState(
                        flowId = flowId,
                        name = flow?.name ?: "",
                        description = flow?.description ?: "",
                        steps = flow?.steps?.map { step ->
                            FlowEditorStepState(
                                command = step.command,
                                label = step.label ?: "",
                                delayAfterMs = step.delayAfterMs.toString(),
                            )
                        } ?: listOf(FlowEditorStepState()),
                    ),
                )
            }
        } else {
            localState.update {
                it.copy(
                    showFlowEditor = true,
                    flowEditorState = FlowEditorState(),
                )
            }
        }
    }

    private fun onDismissFlowEditor() {
        localState.update { it.copy(showFlowEditor = false) }
    }

    private fun onFlowEditorNameChanged(name: String) {
        localState.update {
            it.copy(flowEditorState = it.flowEditorState.copy(name = name))
        }
    }

    private fun onFlowEditorDescriptionChanged(description: String) {
        localState.update {
            it.copy(flowEditorState = it.flowEditorState.copy(description = description))
        }
    }

    private fun onFlowEditorStepCommandChanged(index: Int, command: String) {
        localState.update {
            val steps = it.flowEditorState.steps.toMutableList()
            if (index < steps.size) {
                steps[index] = steps[index].copy(command = command)
            }
            it.copy(flowEditorState = it.flowEditorState.copy(steps = steps))
        }
    }

    private fun onFlowEditorStepLabelChanged(index: Int, label: String) {
        localState.update {
            val steps = it.flowEditorState.steps.toMutableList()
            if (index < steps.size) {
                steps[index] = steps[index].copy(label = label)
            }
            it.copy(flowEditorState = it.flowEditorState.copy(steps = steps))
        }
    }

    private fun onFlowEditorStepDelayChanged(index: Int, delay: String) {
        localState.update {
            val steps = it.flowEditorState.steps.toMutableList()
            if (index < steps.size) {
                steps[index] = steps[index].copy(delayAfterMs = delay)
            }
            it.copy(flowEditorState = it.flowEditorState.copy(steps = steps))
        }
    }

    private fun onFlowEditorAddStep() {
        localState.update {
            it.copy(
                flowEditorState = it.flowEditorState.copy(
                    steps = it.flowEditorState.steps + FlowEditorStepState()
                )
            )
        }
    }

    private fun onFlowEditorRemoveStep(index: Int) {
        localState.update {
            val steps = it.flowEditorState.steps.toMutableList()
            if (steps.size > 1 && index < steps.size) {
                steps.removeAt(index)
            }
            it.copy(flowEditorState = it.flowEditorState.copy(steps = steps))
        }
    }

    private fun onSaveFlow() {
        val editor = localState.value.flowEditorState
        if (editor.name.isBlank()) {
            feedbackDisplayer.displayMessage(
                "Please enter a flow name",
                type = FeedbackDisplayer.MessageType.Error,
            )
            return
        }
        if (editor.steps.any { it.command.isBlank() }) {
            feedbackDisplayer.displayMessage(
                "All steps must have a command",
                type = FeedbackDisplayer.MessageType.Error,
            )
            return
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            val flow = AdbFlowDomainModel(
                id = editor.flowId ?: 0,
                name = editor.name,
                description = editor.description.ifBlank { null },
                steps = editor.steps.mapIndexed { index, step ->
                    AdbFlowStepDomainModel(
                        id = 0,
                        orderIndex = index,
                        command = step.command,
                        delayAfterMs = step.delayAfterMs.toLongOrNull() ?: 0L,
                        label = step.label.ifBlank { null },
                    )
                },
            )
            if (editor.flowId != null) {
                updateFlowUseCase(flow)
            } else {
                saveFlowUseCase(flow)
            }
            localState.update { it.copy(showFlowEditor = false) }
            feedbackDisplayer.displayMessage("Flow saved")
        }
    }

    private fun onDeleteFlow(id: Long) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteFlowUseCase(id)
            feedbackDisplayer.displayMessage("Flow deleted")
        }
    }

    private fun onExecuteFlow(flowId: Long) {
        val flow = domainFlows.value.find { it.id == flowId } ?: return

        flowExecutionJob?.cancel()
        localState.update { it.copy(selectedTab = AdbCommanderTab.Runner) }

        flowExecutionJob = viewModelScope.launch(dispatcherProvider.viewModel) {
            executeFlowUseCase(flow).collect { state ->
                localState.update { it.copy(flowExecution = state.toUiModel()) }
            }
        }
    }

    private fun onCancelFlowExecution() {
        flowExecutionJob?.cancel()
        flowExecutionJob = null
    }

    private fun onClearConsole() {
        localState.update { it.copy(consoleOutput = emptyList(), flowExecution = null) }
    }
}
