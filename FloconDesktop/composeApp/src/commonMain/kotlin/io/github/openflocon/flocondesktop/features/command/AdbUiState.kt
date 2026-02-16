package io.github.openflocon.flocondesktop.features.command

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class AdbUiState(
    val commands: ImmutableList<AdbCommandUi>
)

internal fun previewAdbUiState() = AdbUiState(
    commands = persistentListOf()
)

@Immutable
internal data class AdbCommandUi(
    val id: Long,
    val command: String,
    val loading: Boolean
)

internal fun AdbCommand.toUi() = AdbCommandUi(
    id = id,
    command = command,
    loading = false
)
