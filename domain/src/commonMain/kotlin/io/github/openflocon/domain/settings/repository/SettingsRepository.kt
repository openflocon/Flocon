package io.github.openflocon.domain.settings.repository

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.versions.model.FloconSdk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

enum class AdbForwardStatus { UNKNOWN, OK, NOK }

interface SettingsRepository {
    var networkSettings: NetworkSettings
    val networkSettingsFlow: Flow<NetworkSettings>

    fun getAdbPath(): String?

    suspend fun setAdbPath(path: String)

    suspend fun setAppScale(value: AppScaleSetting)
    suspend fun setAppLanguage(languageTag: String?)

    suspend fun setTheme(value: ThemeSetting)

    fun getFloconDocumentsPath(): String?

    suspend fun setFloconDocumentsPath(path: String)

    val floconDocumentsPath: Flow<String?>

    fun getDismissedDesktopVersion(): String?

    suspend fun setDismissedDesktopVersion(version: String)

    /** The update dismissed for [sdk]: the SDKs are released apart, their versions can't be compared with each other. */
    fun dismissedClientVersionFlow(sdk: FloconSdk): Flow<String?>

    fun getDismissedClientVersion(sdk: FloconSdk): String?

    suspend fun setDismissedClientVersion(sdk: FloconSdk, version: String)

    fun getLastSeenReleaseNotesVersion(): String?

    suspend fun setLastSeenReleaseNotesVersion(version: String)

    val savedServerPorts: Flow<ServerPortsDomainModel>

    suspend fun setServerPorts(ports: ServerPortsDomainModel)

    val adbPath: Flow<String?>
    val appScale: StateFlow<AppScaleSetting>

    /** BCP 47 tag of the language chosen by the user, null to follow the system one. */
    val appLanguage: StateFlow<String?>
    val theme: StateFlow<ThemeSetting>
    val adbForwardStatus: StateFlow<AdbForwardStatus>

    fun setAdbForwardStatus(status: AdbForwardStatus)
}
