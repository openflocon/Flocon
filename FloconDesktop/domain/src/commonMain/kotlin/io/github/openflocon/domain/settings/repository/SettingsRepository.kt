package io.github.openflocon.domain.settings.repository

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

enum class AdbForwardStatus { UNKNOWN, OK, NOK }

interface SettingsRepository {
    var networkSettings: NetworkSettings
    val networkSettingsFlow: Flow<NetworkSettings>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    suspend fun setFontSizeMultiplier(value: Float)

    suspend fun setTheme(value: ThemeSetting)

    fun getDismissedDesktopVersion(): String?

    suspend fun setDismissedDesktopVersion(version: String)

    val dismissedClientVersionFlow: Flow<String?>

    fun getDismissedClientVersion(): String?

    suspend fun setDismissedClientVersion(version: String)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
    val theme: StateFlow<ThemeSetting>
    val adbForwardStatus: StateFlow<AdbForwardStatus>

    fun setAdbForwardStatus(status: AdbForwardStatus)
}
