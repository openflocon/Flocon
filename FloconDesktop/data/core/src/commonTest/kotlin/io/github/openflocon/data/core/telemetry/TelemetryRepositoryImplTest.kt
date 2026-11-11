package io.github.openflocon.data.core.telemetry

import io.github.openflocon.data.core.telemetry.datasource.TelemetryLocalDataSource
import io.github.openflocon.data.core.telemetry.datasource.TelemetryRemoteDataSource
import io.github.openflocon.data.core.telemetry.repository.TelemetryRepositoryImpl
import io.github.openflocon.domain.telemetry.models.TelemetryConfigDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TelemetryRepositoryImplTest {

    private class FakeLocal(
        var storedLevel: TelemetryLevelDomainModel? = null,
        var storedInstallationId: String? = null,
    ) : TelemetryLocalDataSource {
        override fun getLevel() = storedLevel
        override fun setLevel(level: TelemetryLevelDomainModel) {
            storedLevel = level
        }
        override fun getInstallationId() = storedInstallationId
        override fun setInstallationId(id: String?) {
            storedInstallationId = id
        }
    }

    private data class Captured(val event: String, val distinctId: String, val properties: Map<String, Any>)

    private class FakeRemote : TelemetryRemoteDataSource {
        val events = mutableListOf<Captured>()
        override suspend fun capture(
            config: TelemetryConfigDomainModel,
            event: String,
            distinctId: String,
            properties: Map<String, Any>,
        ) {
            events += Captured(event, distinctId, properties)
        }
    }

    private fun config(apiKey: String = "phc_test", doNotTrack: Boolean = false) = TelemetryConfigDomainModel(
        apiKey = apiKey,
        host = "https://eu.i.posthog.com",
        appVersion = "1.0.0",
        doNotTrack = doNotTrack,
        osName = "TestOS",
        osArch = "test64",
    )

    private fun repository(
        local: FakeLocal,
        remote: FakeRemote,
        config: TelemetryConfigDomainModel = config(),
    ) = TelemetryRepositoryImpl(
        localDataSource = local,
        remoteDataSource = remote,
        config = config,
        applicationScope = CoroutineScope(Dispatchers.Unconfined),
    )

    @Test
    fun `build without api key never sends and never asks consent`() {
        val remote = FakeRemote()
        val repository = repository(FakeLocal(), remote, config(apiKey = ""))

        repository.track("app_started", emptyMap())

        assertTrue(remote.events.isEmpty())
        assertFalse(repository.state.value.shouldAskConsent)
        assertEquals(TelemetryLevelDomainModel.Disabled, repository.state.value.level)
    }

    @Test
    fun `do not track disables everything`() {
        val remote = FakeRemote()
        val repository = repository(FakeLocal(storedLevel = TelemetryLevelDomainModel.Full), remote, config(doNotTrack = true))

        repository.track("app_started", emptyMap())

        assertTrue(remote.events.isEmpty())
        assertFalse(repository.state.value.shouldAskConsent)
    }

    @Test
    fun `undecided user is asked for consent and nothing is sent`() {
        val local = FakeLocal()
        val remote = FakeRemote()
        val repository = repository(local, remote)

        repository.track("app_started", emptyMap())

        assertTrue(repository.state.value.shouldAskConsent)
        assertEquals(TelemetryLevelDomainModel.Disabled, repository.state.value.level)
        assertTrue(remote.events.isEmpty(), "opt-in: nothing is sent before the user answers")
        assertNull(local.storedInstallationId)
    }

    @Test
    fun `events are sent once the user opted in`() {
        val remote = FakeRemote()
        val repository = repository(FakeLocal(), remote)

        repository.setLevel(TelemetryLevelDomainModel.Anonymous)
        repository.track("app_started", emptyMap())

        assertFalse(repository.state.value.shouldAskConsent)
        assertEquals(1, remote.events.size)
    }

    @Test
    fun `anonymous mode uses the session id`() {
        val remote = FakeRemote()
        val repository = repository(FakeLocal(storedLevel = TelemetryLevelDomainModel.Anonymous), remote)

        repository.track("app_started", emptyMap())
        repository.track("screen_viewed", mapOf("screen" to "Network"))

        val (first, second) = remote.events
        assertEquals(first.distinctId, second.distinctId)
        assertEquals(first.distinctId, first.properties["\$session_id"])
        assertEquals(false, first.properties["\$process_person_profile"])
        assertEquals("Network", second.properties["screen"])
        assertEquals("TestOS", first.properties["os_name"])
        assertEquals("Anonymous", first.properties["telemetry_level"])
    }

    @Test
    fun `full mode persists a stable installation id`() {
        val local = FakeLocal(storedLevel = TelemetryLevelDomainModel.Full)
        val remote = FakeRemote()
        val repository = repository(local, remote)

        repository.track("app_started", emptyMap())

        val id = assertNotNull(local.storedInstallationId)
        assertEquals(id, remote.events.single().distinctId)
    }

    @Test
    fun `leaving full mode deletes the installation id`() {
        val local = FakeLocal(storedLevel = TelemetryLevelDomainModel.Full, storedInstallationId = "stored-id")
        val repository = repository(local, FakeRemote())

        repository.setLevel(TelemetryLevelDomainModel.Anonymous)

        assertNull(local.storedInstallationId)
        assertFalse(repository.state.value.shouldAskConsent)
    }

    @Test
    fun `disabled sends nothing`() {
        val remote = FakeRemote()
        val repository = repository(FakeLocal(), remote)

        repository.setLevel(TelemetryLevelDomainModel.Disabled)
        repository.track("app_started", emptyMap())

        assertTrue(remote.events.isEmpty())
    }
}
