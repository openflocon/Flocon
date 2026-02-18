package io.github.openflocon.flocondesktop.features.adbcommander.mapper

import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowExecutionState
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowExecutionStepUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowExecutionUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.FlowUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.HistoryEntryUiModel
import io.github.openflocon.flocondesktop.features.adbcommander.model.SavedCommandUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun AdbCommandDomainModel.toUiModel() = SavedCommandUiModel(
    id = id,
    name = name,
    command = command,
    description = description,
)

fun AdbFlowDomainModel.toUiModel() = FlowUiModel(
    id = id,
    name = name,
    description = description,
    stepsCount = steps.size,
)

fun AdbCommandHistoryDomainModel.toUiModel() = HistoryEntryUiModel(
    id = id,
    command = command,
    output = output.ifEmpty { "(no output)" },
    isSuccess = isSuccess,
    executedAt = formatTimestamp(executedAt),
)

fun AdbFlowExecutionState.toUiModel() = FlowExecutionUiModel(
    flowName = flowName,
    steps = steps.map { stepState ->
        FlowExecutionStepUiModel(
            label = stepState.step.label ?: stepState.step.command,
            command = stepState.step.command,
            status = stepState.status.name,
            output = stepState.output,
            isActive = stepState.status in setOf(
                AdbFlowExecutionState.StepStatus.Running,
                AdbFlowExecutionState.StepStatus.WaitingDelay,
            ),
        )
    },
    status = status.name,
    isRunning = status == AdbFlowExecutionState.FlowStatus.Running,
)

private val timestampFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

private fun formatTimestamp(epochMs: Long): String {
    return timestampFormatter.format(Date(epochMs))
}
