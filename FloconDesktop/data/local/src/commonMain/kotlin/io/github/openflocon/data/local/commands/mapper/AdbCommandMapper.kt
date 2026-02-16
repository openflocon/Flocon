package io.github.openflocon.data.local.commands.mapper

import io.github.openflocon.data.local.commands.model.AdbCommandEntity
import io.github.openflocon.domain.commands.models.AdbCommand

internal fun AdbCommandEntity.toDomain() = AdbCommand(
    id = id,
    command = command
)

internal fun AdbCommand.toLocal() = AdbCommandEntity(
    id = id,
    command = command
)
