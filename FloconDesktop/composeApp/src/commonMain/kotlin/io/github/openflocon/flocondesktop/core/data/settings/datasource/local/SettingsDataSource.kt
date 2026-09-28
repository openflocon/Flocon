package io.github.openflocon.flocondesktop.core.data.settings.datasource.local

import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.flocondesktop.core.data.settings.models.NetworkSettingsLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

internal interface SettingsDataSource {
    var networkSettings: NetworkSettingsLocal
    val networkSettingsFlow: StateFlow<NetworkSettingsLocal>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)
    suspend fun setFontSizeMultiplier(value: Float)
    suspend fun setTheme(value: ThemeSetting)

    fun getDismissedDesktopVersion(): String?
    suspend fun setDismissedDesktopVersion(version: String)

    fun getDismissedClientVersion(): String?
    suspend fun setDismissedClientVersion(version: String)

    val adbPath: Flow<String?>
    val fontSizeMultiplier: StateFlow<Float>
    val theme: StateFlow<ThemeSetting>
    val dismissedClientVersionFlow: Flow<String?>
}
