package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.flocondesktop.common.log.LogEntryUiModel
import io.github.openflocon.flocondesktop.common.log.LogLevel

data class SettingsUiState(
    val fontSizeMultiplier: Float,
    val logs: List<LogEntryUiModel> = emptyList(),
    val theme: ThemeSetting
)

fun previewSettingsUiState() = SettingsUiState(
    fontSizeMultiplier = 1f,
    logs = listOf(
        LogEntryUiModel(LogLevel.DEBUG, "ADB path saved: /usr/local/bin/adb"),
        LogEntryUiModel(LogLevel.ERROR, "ADB test failed: No such file or directory"),
        LogEntryUiModel(LogLevel.DEBUG, "ADB test succeeded"),
    ),
    theme = ThemeSetting.DEFAULT
)

