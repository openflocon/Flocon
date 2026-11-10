package io.github.openflocon.flocondesktop.mock

import com.flocon.data.remote.mock.MockScenario
import com.flocon.data.remote.mock.MockServer
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRepository
import io.github.openflocon.flocondesktop.BuildConfig
import io.github.openflocon.flocondesktop.common.db.DatabaseFile
import io.github.openflocon.flocondesktop.common.db.createMediaFilesRepository
import io.github.openflocon.flocondesktop.common.db.getMockDatabaseFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Loaded last when the app is launched with `--mock=<name>`: overrides the real definitions so the
 * scenario devices look connected, the data goes to a separate database and nothing runs on the real devices.
 */
fun mockModule(scenario: MockScenario): Module = module {
    single<Server> { MockServer(scenario = scenario, json = get(), defaultSdkVersion = BuildConfig.APP_VERSION) }
    single { DatabaseFile(getMockDatabaseFile()) }
    single<AdbRepository> { NoOpAdbRepository }
    single<IosSimulatorRepository> { NoOpIosSimulatorRepository }
    // no adb to take screenshots: a few drawn ones, to see them in the timeline
    single<TimelineScreenshotRepository> { MockTimelineScreenshotRepository() }
    // the auto-clean at launch must not delete the user's real screenshots and recordings
    single<MediaFilesRepository> { KeepMediaFilesRepository(createMediaFilesRepository()) }
}

/** In mock mode, the real media files can be measured but are never deleted. */
internal class KeepMediaFilesRepository(
    private val real: MediaFilesRepository,
) : MediaFilesRepository by real {
    override suspend fun deleteFilesBefore(beforeTimestampMs: Long) = Unit
}

/** In mock mode, adb must not run: a new mocked Android device would broadcast to the real phones. */
internal object NoOpAdbRepository : AdbRepository {
    private val disabled = IllegalStateException("adb is disabled in mock mode")

    override suspend fun getAdbSerial(deviceId: String): String? = null

    override suspend fun saveAdbSerial(deviceId: String, serial: String) = Unit

    override val devicesWithSerial: Flow<Collection<DeviceWithSerialDomainModel>> = flowOf(emptyList())

    override fun executeAdbCommand(
        adbPath: String,
        deviceSerial: String?,
        command: String,
    ): Either<Throwable, String> = disabled.failure()

    override fun executeAdbAskSerialToAllDevices(
        adbPath: String,
        command: String,
        serialVariableName: String,
    ): Either<Throwable, String> = disabled.failure()

    override fun findAdbPath(): String? = null
}

/** In mock mode, the real iOS simulators are left alone. */
internal object NoOpIosSimulatorRepository : IosSimulatorRepository {
    private val disabled = IllegalStateException("the iOS simulator is disabled in mock mode")

    override suspend fun openUrl(url: String): Either<Throwable, String> = disabled.failure()

    override suspend fun takeScreenshot(filePath: String): Either<Throwable, String> = disabled.failure()

    override suspend fun startRecording(filePath: String): Either<Throwable, Unit> = disabled.failure()

    override suspend fun stopRecording(filePath: String): Either<Throwable, String> = disabled.failure()
}
