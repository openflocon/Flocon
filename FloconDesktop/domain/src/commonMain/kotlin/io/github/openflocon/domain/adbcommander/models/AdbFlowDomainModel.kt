package io.github.openflocon.domain.adbcommander.models

data class AdbFlowDomainModel(
    val id: Long,
    val name: String,
    val description: String?,
    val steps: List<AdbFlowStepDomainModel>,
)

data class AdbFlowStepDomainModel(
    val id: Long,
    val orderIndex: Int,
    val command: String,
    val delayAfterMs: Long,
    val label: String?,
)
