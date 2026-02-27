package io.github.openflocon.flocondesktop.features.adbcommander.model

sealed interface AdbCommanderAction {
    data class TabSelected(val tab: AdbCommanderTab) : AdbCommanderAction
    data class CommandInputChanged(val input: String) : AdbCommanderAction
    data object ExecuteCommand : AdbCommanderAction
    data object SaveCurrentCommand : AdbCommanderAction
    data class RunSavedCommand(val command: String) : AdbCommanderAction
    data class DeleteSavedCommand(val id: Long) : AdbCommanderAction
    data class SaveQuickCommand(val name: String, val command: String) : AdbCommanderAction
    data object ClearHistory : AdbCommanderAction
    data class RerunCommand(val command: String) : AdbCommanderAction
    data object ClearConsole : AdbCommanderAction

    // Flow actions
    data class ShowFlowEditor(val flowId: Long? = null) : AdbCommanderAction
    data object DismissFlowEditor : AdbCommanderAction
    data class FlowEditorNameChanged(val name: String) : AdbCommanderAction
    data class FlowEditorDescriptionChanged(val description: String) : AdbCommanderAction
    data class FlowEditorStepCommandChanged(val index: Int, val command: String) : AdbCommanderAction
    data class FlowEditorStepLabelChanged(val index: Int, val label: String) : AdbCommanderAction
    data class FlowEditorStepDelayChanged(val index: Int, val delay: String) : AdbCommanderAction
    data object FlowEditorAddStep : AdbCommanderAction
    data class FlowEditorRemoveStep(val index: Int) : AdbCommanderAction
    data object SaveFlow : AdbCommanderAction
    data class DeleteFlow(val id: Long) : AdbCommanderAction
    data class ExecuteFlow(val flowId: Long) : AdbCommanderAction
    data object CancelFlowExecution : AdbCommanderAction
}
