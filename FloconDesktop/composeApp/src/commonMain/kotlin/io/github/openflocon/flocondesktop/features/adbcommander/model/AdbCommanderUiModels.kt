package io.github.openflocon.flocondesktop.features.adbcommander.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AdbCommanderUiState(
    val commandInput: String = "",
    val consoleOutput: PersistentList<ConsoleOutputEntry> = persistentListOf(),
    val savedCommands: ImmutableList<SavedCommandUiModel> = persistentListOf(),
    val flows: ImmutableList<FlowUiModel> = persistentListOf(),
    val history: ImmutableList<HistoryEntryUiModel> = persistentListOf(),
    val flowExecution: FlowExecutionUiModel? = null,
    val isExecuting: Boolean = false,
    val showFlowEditor: Boolean = false,
    val flowEditorState: FlowEditorState = FlowEditorState(),
)

@Immutable
data class ConsoleOutputEntry(
    val command: String,
    val output: String,
    val isSuccess: Boolean,
)

@Immutable
data class SavedCommandUiModel(
    val id: Long,
    val name: String,
    val command: String,
    val description: String?,
)

@Immutable
data class FlowUiModel(
    val id: Long,
    val name: String,
    val description: String?,
    val stepsCount: Int,
)

@Immutable
data class HistoryEntryUiModel(
    val id: Long,
    val command: String,
    val output: String,
    val isSuccess: Boolean,
    val executedAt: String,
)

@Immutable
data class FlowExecutionUiModel(
    val flowName: String,
    val steps: ImmutableList<FlowExecutionStepUiModel>,
    val status: String,
    val isRunning: Boolean,
)

@Immutable
data class FlowExecutionStepUiModel(
    val label: String,
    val command: String,
    val status: String,
    val output: String?,
    val isActive: Boolean,
)

@Immutable
data class FlowEditorState(
    val flowId: Long? = null,
    val name: String = "",
    val description: String = "",
    val steps: PersistentList<FlowEditorStepState> = persistentListOf(FlowEditorStepState()),
)

@Immutable
data class FlowEditorStepState(
    val command: String = "",
    val label: String = "",
    val delayAfterMs: String = "0",
)
