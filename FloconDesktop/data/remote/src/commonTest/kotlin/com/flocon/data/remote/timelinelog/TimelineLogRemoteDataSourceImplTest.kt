package com.flocon.data.remote.timelinelog

import com.flocon.data.remote.timelinelog.datasource.TimelineLogRemoteDataSourceImpl
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TimelineLogRemoteDataSourceImplTest {

    private val dataSource = TimelineLogRemoteDataSourceImpl(Json { ignoreUnknownKeys = true })

    private fun message(body: String) = FloconIncomingMessageDomainModel(
        deviceName = "Pixel 9",
        deviceId = "pixel",
        plugin = "timeline",
        body = body,
        method = "log",
        appName = "Sample",
        appPackageName = "io.flocon.sample",
        appInstance = 1L,
        platform = "android",
        floconVersionOnDevice = "1.0.0",
    )

    private fun decode(body: String) = assertNotNull(dataSource.getLog(message(body)))

    @Test
    fun `mapsReservedTypeCaseInsensitively`() {
        assertEquals("navigation", decode("""{"type":"NAVIGATION","value":"Home","timestamp":1}""").type)
        assertEquals("userAction", decode("""{"type":"useraction","value":"Tap","timestamp":1}""").type)
        assertEquals("lifecycle", decode("""{"type":"Lifecycle","value":"Resumed","timestamp":1}""").type)
    }

    @Test
    fun `customTagIsKeptAsIs`() {
        assertEquals("Payments", decode("""{"type":"Payments","value":"x","level":"error","timestamp":1}""").type)
    }

    @Test
    fun `level is decoded case insensitively`() {
        assertEquals(TimelineLogLevel.Warning, decode("""{"type":"t","value":"x","level":"warning","timestamp":1}""").level)
        assertEquals(TimelineLogLevel.Error, decode("""{"type":"t","value":"x","level":"ERROR","timestamp":1}""").level)
    }

    @Test
    fun `unknownLevelFallsBackToInfo`() {
        assertEquals(TimelineLogLevel.Info, decode("""{"type":"t","value":"x","level":"verbose","timestamp":1}""").level)
    }

    @Test
    fun `missingLevelFallsBackToInfo`() {
        assertEquals(TimelineLogLevel.Info, decode("""{"type":"navigation","value":"x","timestamp":1}""").level)
    }

    @Test
    fun `absent params default to empty and are decoded when present`() {
        assertEquals(emptyMap(), decode("""{"type":"t","value":"x","timestamp":1}""").params)
        val log = decode("""{"type":"t","value":"x","params":{"a":"1"},"timestamp":42}""")
        assertEquals(mapOf("a" to "1"), log.params)
        assertEquals(42L, log.timestamp)
        assertEquals(0L, log.id)
    }

    @Test
    fun `invalid body gives null`() {
        assertNull(dataSource.getLog(message("not json")))
    }
}
