package io.github.openflocon.domain.adbcommander.models

data class AdbFlowExecutionState(
    val flowName: String,
    val steps: List<StepState>,
    val currentStepIndex: Int,
    val status: FlowStatus,
) {
    enum class FlowStatus { Running, Completed, Cancelled, Failed }

    data class StepState(
        val step: AdbFlowStepDomainModel,
        val status: StepStatus,
        val output: String?,
    )

    enum class StepStatus { Pending, Running, WaitingDelay, Completed, Failed, Skipped }
}
