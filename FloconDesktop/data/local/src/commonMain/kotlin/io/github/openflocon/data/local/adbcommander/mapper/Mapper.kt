package io.github.openflocon.data.local.adbcommander.mapper

import io.github.openflocon.data.local.adbcommander.models.AdbCommandHistoryEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowStepEntity
import io.github.openflocon.data.local.adbcommander.models.AdbSavedCommandEntity
import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowStepDomainModel

fun AdbSavedCommandEntity.toDomainModel() = AdbCommandDomainModel(
    id = id,
    name = name,
    command = command,
    description = description,
)

fun AdbCommandDomainModel.toEntity(deviceId: String) = AdbSavedCommandEntity(
    id = id,
    deviceId = deviceId,
    name = name,
    command = command,
    description = description,
    createdAt = System.currentTimeMillis(),
)

fun AdbCommandHistoryEntity.toDomainModel() = AdbCommandHistoryDomainModel(
    id = id,
    command = command,
    output = output,
    isSuccess = isSuccess,
    executedAt = executedAt,
)

fun AdbFlowEntity.toDomainModel(steps: List<AdbFlowStepEntity>) = AdbFlowDomainModel(
    id = id,
    name = name,
    description = description,
    steps = steps.map { it.toDomainModel() },
)

fun AdbFlowStepEntity.toDomainModel() = AdbFlowStepDomainModel(
    id = id,
    orderIndex = orderIndex,
    command = command,
    delayAfterMs = delayAfterMs,
    label = label,
)

fun AdbFlowStepDomainModel.toEntity(flowId: Long) = AdbFlowStepEntity(
    id = id,
    flowId = flowId,
    orderIndex = orderIndex,
    command = command,
    delayAfterMs = delayAfterMs,
    label = label,
)
