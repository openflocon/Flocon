package io.github.openflocon.data.local.timelinelog

import io.github.openflocon.data.local.timelinelog.datasource.toDomain
import io.github.openflocon.data.local.timelinelog.datasource.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineLogMapperTest {

    private val device = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 7L)

    private fun log(level: TimelineLogLevel = TimelineLogLevel.Info, params: Map<String, String> = emptyMap()) = TimelineLogDomainModel(
        id = 0,
        timestamp = 100L,
        type = "userAction",
        level = level,
        value = "Tap",
        params = params,
    )

    @Test
    fun `levelStoredAsUpperCaseEnumName`() {
        assertEquals("DEBUG", log(TimelineLogLevel.Debug).toEntity(device).level)
        assertEquals("INFO", log(TimelineLogLevel.Info).toEntity(device).level)
        assertEquals("WARNING", log(TimelineLogLevel.Warning).toEntity(device).level)
        assertEquals("ERROR", log(TimelineLogLevel.Error).toEntity(device).level)
    }

    @Test
    fun `entity carries the app identity and the type as is`() {
        val entity = log().toEntity(device)
        assertEquals("pixel", entity.deviceId)
        assertEquals("io.flocon.sample", entity.packageName)
        assertEquals(7L, entity.appInstance)
        assertEquals("userAction", entity.type)
        assertEquals(100L, entity.timestamp)
    }

    @Test
    fun `paramsRoundTripWithQuotesNewlinesUnicode`() {
        val params = mapOf(
            "quote" to "say \"hi\"",
            "newline" to "a\nb\r\nc",
            "unicode" to "caf\u00e9 \u2603 \uD83D\uDE00",
            "backslash" to "C:\\tmp",
            "empty" to "",
        )
        assertEquals(params, log(params = params).toEntity(device).toDomain().params)
    }

    @Test
    fun `unknown stored level reads back as Info`() {
        assertEquals(TimelineLogLevel.Info, log().toEntity(device).copy(level = "TRACE").toDomain().level)
    }

    @Test
    fun `level reads back`() {
        TimelineLogLevel.entries.forEach {
            assertEquals(it, log(it).toEntity(device).toDomain().level)
        }
    }
}
