package io.github.openflocon.flocondesktop.common.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.openflocon.data.core.timeline.datasources.TimelineLocalDataSource
import io.github.openflocon.data.local.dataLocalModule
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.data.local.timelinelog.models.TimelineLogEntity
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Runs the real timeline queries (the union of the sources, the facet counts) against a real in-memory database.
 */
class TimelineLogQueryTest {

    private fun log(timestamp: Long, type: String, level: String, value: String) = TimelineLogEntity(
        deviceId = "pixel",
        packageName = "io.flocon.sample",
        appInstance = 1L,
        timestamp = timestamp,
        type = type,
        level = level,
        value = value,
        paramsJson = "{}",
    )

    private val app = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)

    private fun withLogs(logs: List<TimelineLogEntity>, block: suspend (TimelineLocalDataSource) -> Unit) = runBlocking {
        val db = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
        val koin = koinApplication {
            modules(
                dataLocalModule,
                module {
                    single { db.timelineDao }
                    single<DispatcherProvider> {
                        object : DispatcherProvider {
                            override val ui = Dispatchers.Unconfined
                            override val viewModel = Dispatchers.Unconfined
                            override val domain = Dispatchers.Unconfined
                            override val data = Dispatchers.Unconfined
                        }
                    }
                },
            )
        }.koin
        try {
            db.devicesDao.insertDevice(DeviceEntity(deviceId = "pixel", deviceName = "Pixel", platform = "android"))
            db.devicesDao.insertDeviceApp(
                DeviceAppEntity(
                    deviceId = "pixel",
                    name = "io.flocon.sample",
                    packageName = "io.flocon.sample",
                    iconEncoded = null,
                    lastAppInstance = 1L,
                    floconVersionOnDevice = "1.0.0",
                ),
            )
            logs.forEach { db.timelineLogDao.insert(it) }
            block(koin.get<TimelineLocalDataSource>())
        } finally {
            koin.close()
            db.close()
        }
    }

    @Test
    fun `logsAreReturnedByTheTimelineQueryAndCountedByFacet`() = withLogs(
        listOf(
            log(timestamp = 100, type = "custom", level = "ERROR", value = "payment failed"),
            log(timestamp = 200, type = "navigation", level = "INFO", value = "home"),
            log(timestamp = 300, type = "billing", level = "INFO", value = "invoice created"),
        ),
    ) { dataSource ->
        val filter = TimelineFilterDomainModel(sources = setOf(TimelineSource.Log))

        // newest first
        val events = dataSource.observeTimelineEvents(app, filter).first()
        assertEquals(listOf("invoice created", "home", "payment failed"), events.map { it.title })
        assertEquals(setOf(TimelineSource.Log), events.map { it.source }.toSet())
        assertEquals(listOf("billing", "navigation", "custom"), events.map { it.subtitle })
        assertEquals(listOf(TimelineLogLevel.Info, TimelineLogLevel.Info, TimelineLogLevel.Error), events.map { it.level })
        assertEquals(listOf(false, false, true), events.map { it.hasError })

        val facets = dataSource.observeSummary(app, filter).first().facetCounts
        assertEquals(mapOf("custom" to 1, "navigation" to 1, "billing" to 1), facets[TimelineFacet.LogType])
        assertEquals(mapOf("ERROR" to 1, "INFO" to 2), facets[TimelineFacet.LogLevel])
        // the reserved types have no level
        assertEquals(mapOf("ERROR" to 1, "INFO" to 1), facets[TimelineFacet.LogTaggedLevel])
    }

    private val mixedLogs = listOf(
        log(timestamp = 100, type = "navigation", level = "INFO", value = "home"),
        log(timestamp = 200, type = "custom", level = "ERROR", value = "payment failed"),
        log(timestamp = 300, type = "billing", level = "DEBUG", value = "invoice created"),
        log(timestamp = 400, type = "bill_x", level = "DEBUG", value = "invoice x"),
    )

    private suspend fun titles(dataSource: TimelineLocalDataSource, filter: TimelineFilterDomainModel): List<String> = dataSource
        .observeTimelineEvents(app, filter.copy(sources = setOf(TimelineSource.Log)))
        .first()
        .map { it.title }

    @Test
    fun `hiding the info level keeps the reserved types`() = withLogs(mixedLogs + log(500, "billing", "INFO", "paid")) { dataSource ->
        val hidden = TimelineFilterDomainModel(facets = mapOf(TimelineFacet.LogTaggedLevel to setOf("INFO")))
        assertEquals(listOf("invoice x", "invoice created", "payment failed", "home"), titles(dataSource, hidden))
    }

    @Test
    fun `an include rule keeps the matching tags and the reserved types`() = withLogs(mixedLogs) { dataSource ->
        val filter = TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("bill", exclude = false)))
        assertEquals(listOf("invoice x", "invoice created", "home"), titles(dataSource, filter))
        // case-insensitive
        val upper = TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("BILL", exclude = false)))
        assertEquals(listOf("invoice x", "invoice created", "home"), titles(dataSource, upper))
    }

    @Test
    fun `an exclude rule hides the matching tags and escapes the wildcards`() = withLogs(mixedLogs) { dataSource ->
        val plain = TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("bill", exclude = true)))
        assertEquals(listOf("payment failed", "home"), titles(dataSource, plain))
        // "_" is the character, not "any character": only bill_x has it
        val underscore = TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("_", exclude = true)))
        assertEquals(listOf("invoice created", "payment failed", "home"), titles(dataSource, underscore))
    }

    @Test
    fun `tagged logs disabled leaves the reserved types only`() = withLogs(mixedLogs) { dataSource ->
        assertEquals(listOf("home"), titles(dataSource, TimelineFilterDomainModel(logTaggedEnabled = false)))
    }

    @Test
    fun `the tagged levels are counted without the reserved types`() = withLogs(mixedLogs) { dataSource ->
        val facets = dataSource.observeSummary(app, TimelineFilterDomainModel(sources = setOf(TimelineSource.Log))).first().facetCounts
        assertEquals(mapOf("ERROR" to 1, "DEBUG" to 2), facets[TimelineFacet.LogTaggedLevel])
        assertEquals(mapOf("INFO" to 1, "ERROR" to 1, "DEBUG" to 2), facets[TimelineFacet.LogLevel])
    }
}
