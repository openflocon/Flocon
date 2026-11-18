package io.github.openflocon.flocondesktop.mock

import com.flocon.data.remote.mock.MockScenarios
import com.flocon.data.remote.mock.MockServer
import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.iossimulator.repository.IosSimulatorRepository
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.flocondesktop.common.db.DatabaseFile
import io.github.openflocon.flocondesktop.common.db.getDatabaseFile
import io.github.openflocon.flocondesktop.common.db.getMockDatabaseFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MockModuleTest {

    private val koin = koinApplication {
        modules(
            module {
                single { Json { ignoreUnknownKeys = true } }
                single<Server> { RealServer }
                single { DatabaseFile(getDatabaseFile()) }
            },
            mockModule(MockScenarios.byName("empty")!!),
        )
    }.koin

    @Test
    fun `mock module replaces the server`() {
        assertIs<MockServer>(koin.get<Server>())
    }

    @Test
    fun `mock module uses the mock database file`() {
        assertEquals(DatabaseFile(getMockDatabaseFile()), koin.get<DatabaseFile>())
    }

    @Test
    fun `mock module disables adb`() = runBlocking<Unit> {
        val adb = koin.get<AdbRepository>()
        assertSame(NoOpAdbRepository, adb)
        assertNull(adb.findAdbPath())
        assertTrue(adb.devicesWithSerial.first().isEmpty())
        assertIs<Failure<Throwable>>(adb.executeAdbAskSerialToAllDevices("adb", "shell", "serial"))
    }

    @Test
    fun `mock module disables the iOS simulator`() = runBlocking<Unit> {
        val ios = koin.get<IosSimulatorRepository>()
        assertSame(NoOpIosSimulatorRepository, ios)
        assertIs<Failure<Throwable>>(ios.openUrl("app://home"))
    }
}

private object RealServer : Server {
    override fun startWebsocket(port: Int) = Unit
    override fun starHttp(port: Int) = Unit
    override fun stop() = Unit
    override val receivedMessages: Flow<FloconIncomingMessageDataModel> = emptyFlow()
    override suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    ) = Unit
    override val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>> = emptyFlow()
    override val receivedFiles: Flow<FloconReceivedFileDomainModel> = emptyFlow()
}
