package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.flocondesktop.common.log.LogEntryUiModel
import io.github.openflocon.flocondesktop.common.log.LogLevel

@Immutable
data class SettingsUiState(
    val fontSizeMultiplier: Float,
    val logs: List<LogEntryUiModel>,
    val adbForwardStatus: AdbForwardStatus,
    val theme: ThemeSetting,
    val serverError: String? = null
)

fun previewSettingsUiState() = SettingsUiState(
    fontSizeMultiplier = 1f,
    adbForwardStatus = AdbForwardStatus.OK,
    logs = listOf(
        LogEntryUiModel(LogLevel.DEBUG, "ADB path saved: /usr/local/bin/adb", "12:00:00"),
        LogEntryUiModel(LogLevel.ERROR, "ADB test failed: No such file or directory", "12:00:01"),
        LogEntryUiModel(LogLevel.DEBUG, "ADB test succeeded", "12:00:02"),
    ),
    theme = ThemeSetting.DEFAULT
)

