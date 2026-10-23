package io.github.openflocon.domain.device.usecase

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFloconDocumentPathUseCaseTest {

    private class FakeSettingsRepository(
        var pathValue: String? = null
    ) : SettingsRepository {
        override var networkSettings: NetworkSettings = NetworkSettings(
            pinnedDetails = false,
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
        override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
        override fun getAdbPath(): String? = null
        override suspend fun setAdbPath(path: String) {}
        override suspend fun setAppScale(value: AppScaleSetting) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = pathValue
        override suspend fun setFloconDocumentsPath(path: String) {
            pathValue = path
        }
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = null
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }

    @Test
    fun `when configured path is null then returns default path`() {
        val repo = FakeSettingsRepository(pathValue = null)
        val useCase = GetFloconDocumentPathUseCase(repo)

        val result = useCase()

        assertEquals(GetFloconDocumentPathUseCase.defaultPath(), result)
    }

    @Test
    fun `when configured path is blank then returns default path`() {
        val repo = FakeSettingsRepository(pathValue = "   ")
        val useCase = GetFloconDocumentPathUseCase(repo)

        val result = useCase()

        assertEquals(GetFloconDocumentPathUseCase.defaultPath(), result)
    }

    @Test
    fun `when configured path is set then returns configured file`() {
        val customPath = "/custom/path/to/flocon"
        val repo = FakeSettingsRepository(pathValue = customPath)
        val useCase = GetFloconDocumentPathUseCase(repo)

        val result = useCase()

        assertEquals(File(customPath), result)
    }
}
