package io.github.openflocon.domain.versions

import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.usecase.ShouldShowReleaseNotesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShouldShowReleaseNotesUseCaseTest {

    private class FakeSettingsRepository(
        var lastSeenVersion: String? = null,
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
        override suspend fun setFontSizeMultiplier(value: Float) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = null
        override suspend fun setFloconDocumentsPath(path: String) {}
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = lastSeenVersion
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {
            lastSeenVersion = version
        }
        override val adbPath: Flow<String?> = emptyFlow()
        override val fontSizeMultiplier: StateFlow<Float> = MutableStateFlow(1f)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }

    @Test
    fun `fresh install does not show release notes but records version`() = runBlocking {
        val settings = FakeSettingsRepository(lastSeenVersion = null)

        assertFalse(ShouldShowReleaseNotesUseCase(settings)(current = "2.0.0"))
        assertEquals("2.0.0", settings.lastSeenVersion)
    }

    @Test
    fun `same version does not show release notes`() = runBlocking {
        val settings = FakeSettingsRepository(lastSeenVersion = "2.0.0")

        assertFalse(ShouldShowReleaseNotesUseCase(settings)(current = "2.0.0"))
        assertEquals("2.0.0", settings.lastSeenVersion)
    }

    @Test
    fun `new version shows release notes once`() = runBlocking {
        val settings = FakeSettingsRepository(lastSeenVersion = "1.9.0")
        val useCase = ShouldShowReleaseNotesUseCase(settings)

        assertTrue(useCase(current = "2.0.0"))
        assertEquals("2.0.0", settings.lastSeenVersion)
        assertFalse(useCase(current = "2.0.0"))
    }
}
