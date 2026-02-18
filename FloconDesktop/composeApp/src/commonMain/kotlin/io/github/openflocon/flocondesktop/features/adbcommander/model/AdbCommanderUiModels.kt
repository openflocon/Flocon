package io.github.openflocon.flocondesktop.features.adbcommander.model

import androidx.compose.runtime.Immutable

enum class AdbCommanderTab {
    Runner,
    Library,
    History,
}

@Immutable
data class AdbCommanderUiState(
    val selectedTab: AdbCommanderTab = AdbCommanderTab.Runner,
    val commandInput: String = "",
    val consoleOutput: List<ConsoleOutputEntry> = emptyList(),
    val savedCommands: List<SavedCommandUiModel> = emptyList(),
    val flows: List<FlowUiModel> = emptyList(),
    val history: List<HistoryEntryUiModel> = emptyList(),
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
    val steps: List<FlowExecutionStepUiModel>,
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
    val steps: List<FlowEditorStepState> = listOf(FlowEditorStepState()),
)

@Immutable
data class FlowEditorStepState(
    val command: String = "",
    val label: String = "",
    val delayAfterMs: String = "0",
)
