package io.github.openflocon.domain.versions

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.repository.VersionsCheckerRepository
import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.isRemoteVersionNewer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CheckIsDesktopOnLastVersionUseCaseTest {

    private class FakeVersionsCheckerRepository(
        var remoteVersion: String = "1.0.0",
    ) : VersionsCheckerRepository {
        override val lastVersion: Flow<String?> = emptyFlow()
        override suspend fun checkIsLastVersion(): Either<Throwable, String> = Success(remoteVersion)
    }

    private class FakeSettingsRepository(
        var dismissedVersion: String? = null,
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
        override fun getDismissedDesktopVersion(): String? = dismissedVersion
        override suspend fun setDismissedDesktopVersion(version: String) {
            dismissedVersion = version
        }
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val fontSizeMultiplier: StateFlow<Float> = MutableStateFlow(1f)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }

    @Test
    fun `when remote version is newer and no dismissed version, returns NewVersionAvailable`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(remoteVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(dismissedVersion = null)
            val useCase = CheckIsDesktopOnLastVersionUseCase(
                versionsCheckerRepository = versionsRepo,
                settingsRepository = settingsRepo,
            )

            val result = useCase(current = "1.0.0")

            assertIs<Success<IsLastVersionDomainModel>>(result)
            val model = result.value
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(model)
            assertEquals("1.1.0", model.name)
            assertEquals("1.0.0", model.oldVersion)
        }
    }

    @Test
    fun `when remote version is newer and dismissed version is equal to remote version, returns RunningLastVersion`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(remoteVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(dismissedVersion = "1.1.0")
            val useCase = CheckIsDesktopOnLastVersionUseCase(
                versionsCheckerRepository = versionsRepo,
                settingsRepository = settingsRepo,
            )

            val result = useCase(current = "1.0.0")

            assertIs<Success<IsLastVersionDomainModel>>(result)
            val model = result.value
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(model)
        }
    }

    @Test
    fun `when remote version is newer than both current and dismissed version, returns NewVersionAvailable`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(remoteVersion = "1.2.0")
            val settingsRepo = FakeSettingsRepository(dismissedVersion = "1.1.0")
            val useCase = CheckIsDesktopOnLastVersionUseCase(
                versionsCheckerRepository = versionsRepo,
                settingsRepository = settingsRepo,
            )

            val result = useCase(current = "1.0.0")

            assertIs<Success<IsLastVersionDomainModel>>(result)
            val model = result.value
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(model)
            assertEquals("1.2.0", model.name)
            assertEquals("1.0.0", model.oldVersion)
        }
    }

    @Test
    fun `when remote version is equal or older than current version, returns RunningLastVersion`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(remoteVersion = "1.0.0")
            val settingsRepo = FakeSettingsRepository(dismissedVersion = null)
            val useCase = CheckIsDesktopOnLastVersionUseCase(
                versionsCheckerRepository = versionsRepo,
                settingsRepository = settingsRepo,
            )

            val result = useCase(current = "1.0.0")

            assertIs<Success<IsLastVersionDomainModel>>(result)
            val model = result.value
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(model)
        }
    }

    @Test
    fun `test DismissDesktopVersionUseCase saves dismissed version`() {
        runBlocking {
            val settingsRepo = FakeSettingsRepository()
            val dismissUseCase = io.github.openflocon.domain.versions.usecase.DismissDesktopVersionUseCase(settingsRepo)

            dismissUseCase("1.1.0")

            assertEquals("1.1.0", settingsRepo.getDismissedDesktopVersion())
        }
    }

    @Test
    fun `test complete lifecycle of dismissing version and receiving newer version`() {
        runBlocking {
            val versionsRepo = FakeVersionsCheckerRepository(remoteVersion = "1.1.0")
            val settingsRepo = FakeSettingsRepository(dismissedVersion = null)
            val checkUseCase = CheckIsDesktopOnLastVersionUseCase(
                versionsCheckerRepository = versionsRepo,
                settingsRepository = settingsRepo,
            )
            val dismissUseCase = io.github.openflocon.domain.versions.usecase.DismissDesktopVersionUseCase(settingsRepo)

            // Step 1: 1.1.0 is available, notification shown
            val result1 = checkUseCase(current = "1.0.0")
            assertIs<Success<IsLastVersionDomainModel>>(result1)
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(result1.value)

            // Step 2: User dismisses 1.1.0
            dismissUseCase("1.1.0")

            // Step 3: Next launch, still on 1.0.0 and remote is 1.1.0 -> suppressed
            val result2 = checkUseCase(current = "1.0.0")
            assertIs<Success<IsLastVersionDomainModel>>(result2)
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(result2.value)

            // Step 4: New release 1.2.0 published -> notification shown again
            versionsRepo.remoteVersion = "1.2.0"
            val result3 = checkUseCase(current = "1.0.0")
            assertIs<Success<IsLastVersionDomainModel>>(result3)
            val model3 = result3.value
            assertIs<IsLastVersionDomainModel.NewVersionAvailable>(model3)
            assertEquals("1.2.0", model3.name)

            // Step 5: User updates app to 1.2.0 -> no notification
            val result4 = checkUseCase(current = "1.2.0")
            assertIs<Success<IsLastVersionDomainModel>>(result4)
            assertIs<IsLastVersionDomainModel.RunningLastVersion>(result4.value)
        }
    }

    @Test
    fun `test isRemoteVersionNewer comparisons`() {
        assertTrue(isRemoteVersionNewer(localVersion = "1.0.0", remoteVersion = "1.0.1"))
        assertTrue(isRemoteVersionNewer(localVersion = "1.0.0", remoteVersion = "1.1.0"))
        assertTrue(isRemoteVersionNewer(localVersion = "1.0.0", remoteVersion = "2.0.0"))
        assertTrue(isRemoteVersionNewer(localVersion = "1.1.0", remoteVersion = "1.2.0"))

        assertFalse(isRemoteVersionNewer(localVersion = "1.1.0", remoteVersion = "1.1.0"))
        assertFalse(isRemoteVersionNewer(localVersion = "1.2.0", remoteVersion = "1.1.0"))
        assertFalse(isRemoteVersionNewer(localVersion = "2.0.0", remoteVersion = "1.9.9"))
    }
}
