package io.github.openflocon.flocondesktop.features.command

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AdbUiState(
        val commands: ImmutableList<AdbCommandUi>,
        val history: ImmutableList<AdbCommandUi> = persistentListOf()
)

fun previewAdbUiState() = AdbUiState(commands = persistentListOf(), history = persistentListOf())

@Immutable
data class AdbCommandUi(
        val id: Long,
        val command: String,
        val loading: Boolean = false,
        val output: String? = null
)

fun AdbCommand.toUi() = AdbCommandUi(id = id, command = command, loading = false)
