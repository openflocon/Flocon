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

    @Test
    fun `logsAreReturnedByTheTimelineQueryAndCountedByFacet`() = runBlocking {
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
            db.timelineLogDao.insert(log(timestamp = 100, type = "custom", level = "ERROR", value = "payment failed"))
            db.timelineLogDao.insert(log(timestamp = 200, type = "navigation", level = "INFO", value = "home"))
            db.timelineLogDao.insert(log(timestamp = 300, type = "billing", level = "INFO", value = "invoice created"))

            val dataSource = koin.get<TimelineLocalDataSource>()
            val app = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)
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
        } finally {
            koin.close()
            db.close()
        }
    }
}
