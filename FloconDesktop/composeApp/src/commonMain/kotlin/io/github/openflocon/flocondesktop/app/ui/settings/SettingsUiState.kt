package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.flocondesktop.common.log.LogDomain
import io.github.openflocon.flocondesktop.common.log.LogEntryUiModel
import io.github.openflocon.flocondesktop.common.log.LogLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SettingsUiState(
    val fontSizeMultiplier: Float,
    val logs: ImmutableList<LogEntryUiModel>,
    val adbForwardStatus: AdbForwardStatus,
    val theme: ThemeSetting,
    val selectedLogDomains: Set<LogDomain>,
    val selectedLogLevels: Set<LogLevel>,
)

fun previewSettingsUiState() = SettingsUiState(
    fontSizeMultiplier = 1f,
    adbForwardStatus = AdbForwardStatus.OK,
    logs = persistentListOf(
        LogEntryUiModel(LogLevel.DEBUG, "ADB path saved: /usr/local/bin/adb", "12:00:00", LogDomain.Default),
        LogEntryUiModel(LogLevel.ERROR, "ADB test failed: No such file or directory", "12:00:01", LogDomain.Default),
        LogEntryUiModel(LogLevel.DEBUG, "ADB test succeeded", "12:00:02", LogDomain.Default),
        LogEntryUiModel(LogLevel.INFO, "[MCP] list_devices ok in 12 ms", "12:00:03", LogDomain.Mcp),
    ),
    theme = ThemeSetting.DEFAULT,
    selectedLogDomains = LogDomain.entries.toSet(),
    selectedLogLevels = LogLevel.entries.toSet(),
)

