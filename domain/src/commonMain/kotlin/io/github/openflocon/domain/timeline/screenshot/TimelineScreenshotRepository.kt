package io.github.openflocon.domain.timeline.screenshot

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import java.io.File

// the screenshots of the timeline live in a temporary folder: they are deleted at the next launch
interface TimelineScreenshotRepository {
    // oldest first
    fun observe(target: DeviceIdAndPackageNameDomainModel): Flow<List<TimelineScreenshotDomainModel>>

    // where the jpeg of a screenshot taken at [timestamp] is written, its folder exists
    fun newJpegFile(target: DeviceIdAndPackageNameDomainModel, timestamp: Long): File

    // the oldest ones are deleted past the cap
    suspend fun add(screenshot: TimelineScreenshotDomainModel)

    suspend fun purgeAll()
}
