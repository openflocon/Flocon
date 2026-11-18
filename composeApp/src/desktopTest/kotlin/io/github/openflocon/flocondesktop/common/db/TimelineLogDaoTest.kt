package io.github.openflocon.flocondesktop.common.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.data.local.timelinelog.models.TimelineLogEntity
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineLogDaoTest {

    private fun entity(timestamp: Long, packageName: String = "io.flocon.sample") = TimelineLogEntity(
        deviceId = "pixel",
        packageName = packageName,
        appInstance = 1L,
        timestamp = timestamp,
        type = "navigation",
        level = "INFO",
        value = "v$timestamp",
        paramsJson = "{}",
    )

    @Test
    fun `insertTrimsOldestRowsBeyondCap`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
        try {
            db.devicesDao.insertDevice(DeviceEntity(deviceId = "pixel", deviceName = "Pixel", platform = "android"))
            listOf("io.flocon.sample", "io.flocon.other").forEach {
                db.devicesDao.insertDeviceApp(
                    DeviceAppEntity(
                        deviceId = "pixel",
                        name = it,
                        packageName = it,
                        iconEncoded = null,
                        lastAppInstance = 1L,
                        floconVersionOnDevice = "1.0.0",
                    ),
                )
            }
            val dao = db.timelineLogDao
            dao.insertAndTrim(entity(timestamp = 1, packageName = "io.flocon.other"), max = 3)
            (1L..5L).forEach { dao.insertAndTrim(entity(timestamp = it), max = 3) }

            // only the 3 newest remain for the trimmed app, the other app is untouched
            assertEquals(listOf(3L, 4L, 5L), dao.getAll("pixel", "io.flocon.sample").map { it.timestamp })
            assertEquals(1, dao.getAll("pixel", "io.flocon.other").size)

            // the default cap keeps everything below it
            dao.insertAndTrim(entity(timestamp = 6))
            assertEquals(4, dao.getAll("pixel", "io.flocon.sample").size)
        } finally {
            db.close()
        }
    }
}
