package io.github.openflocon.flocon.network.core.plugin

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.network.core.datasource.FloconNetworkDataSource
import io.github.openflocon.flocon.network.core.mapper.encodeBadQualityConfig
import io.github.openflocon.flocon.network.core.mapper.encodeMocks
import io.github.openflocon.flocon.network.core.model.BadQualityConfig
import io.github.openflocon.flocon.network.core.model.MockNetworkResponse
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FloconNetworkPluginImplTest {

    private val encoder = FloconEncoder()

    private val savedMock = mock(url = ".*/saved")
    private val serverMock = mock(url = ".*/server")
    private val savedConfig = badQualityConfig(errorProbability = 0.1)
    private val serverConfig = badQualityConfig(errorProbability = 0.9)

    private val dataSource = FakeDataSource(savedMocks = listOf(savedMock), savedConfig = savedConfig)

    private fun TestScope.buildPlugin(): FloconNetworkPluginImpl = FloconNetworkPluginImpl(
        context = FloconContext(appName = "app", packageName = "com.app"),
        sender = NoOpSender,
        coroutineScope = backgroundScope,
        encoder = encoder,
        dataSource = dataSource,
    )

    @Test
    fun `the saved mocks and bad network config are not read on the calling thread`() = runTest {
        val plugin = buildPlugin()

        assertEquals(0, dataSource.loadCount)
        assertTrue(plugin.mocks.isEmpty())
        assertNull(plugin.badQualityConfig)
    }

    @Test
    fun `the saved mocks and bad network config are loaded in the background`() = runTest {
        val plugin = buildPlugin()

        runCurrent()

        assertEquals(listOf(savedMock), plugin.mocks)
        assertEquals(savedConfig, plugin.badQualityConfig)
    }

    @Test
    fun `the mocks pushed by the server before the load are not replaced by the saved ones`() = runTest {
        val plugin = buildPlugin()

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Network.Method.SetupMocks,
            body = encoder.encodeMocks(listOf(serverMock)),
        )
        runCurrent()

        assertEquals(2, dataSource.loadCount)
        assertEquals(listOf(serverMock), plugin.mocks)
        assertEquals(listOf(serverMock), dataSource.writtenMocks)
    }

    @Test
    fun `the bad network config pushed by the server before the load is not replaced by the saved one`() = runTest {
        val plugin = buildPlugin()

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Network.Method.SetupBadNetworkConfig,
            body = encoder.encodeBadQualityConfig(serverConfig),
        )
        runCurrent()

        assertEquals(2, dataSource.loadCount)
        assertEquals(0.9, plugin.badQualityConfig?.errorProbability)
    }

    @Test
    fun `the mocks pushed by the server after the load replace the saved ones`() = runTest {
        val plugin = buildPlugin()
        runCurrent()
        assertEquals(listOf(savedMock), plugin.mocks)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Network.Method.SetupMocks,
            body = encoder.encodeMocks(listOf(serverMock)),
        )

        assertEquals(listOf(serverMock), plugin.mocks)
    }

    private fun mock(url: String) = MockNetworkResponse(
        expectation = MockNetworkResponse.Expectation(urlPattern = url, method = "GET"),
        response = MockNetworkResponse.Response.Body(
            httpCode = 418,
            body = "{}",
            delay = 0,
            mediaType = "application/json",
            headers = emptyMap(),
        ),
    )

    private fun badQualityConfig(errorProbability: Double) = BadQualityConfig(
        latency = BadQualityConfig.LatencyConfig(
            latencyTriggerProbability = 0f,
            minLatencyMs = 0,
            maxLatencyMs = 0,
        ),
        errorProbability = errorProbability,
        errors = emptyList(),
    )

    // returns what was on disk when the plugin started: models a read that began before the server pushed a value
    private class FakeDataSource(
        private val savedMocks: List<MockNetworkResponse>,
        private val savedConfig: BadQualityConfig?,
    ) : FloconNetworkDataSource {
        var loadCount = 0
        var writtenMocks: List<MockNetworkResponse>? = null

        override fun saveMocksToFile(mocks: List<MockNetworkResponse>) {
            writtenMocks = mocks
        }

        override fun loadMocksFromFile(): List<MockNetworkResponse> {
            loadCount++
            return savedMocks
        }

        override fun saveBadNetworkConfig(config: BadQualityConfig?) = Unit

        override fun loadBadNetworkConfig(): BadQualityConfig? {
            loadCount++
            return savedConfig
        }
    }

    private object NoOpSender : FloconMessageSender {
        override fun send(plugin: String, method: String, body: String) = Unit
        override fun sendPendingMessages() = Unit
    }
}
