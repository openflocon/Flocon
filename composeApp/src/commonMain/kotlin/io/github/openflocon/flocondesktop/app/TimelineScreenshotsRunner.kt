package io.github.openflocon.flocondesktop.app

import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRecorder
import io.github.openflocon.domain.timeline.usecase.PurgeTimelineScreenshotsUseCase
import kotlinx.coroutines.CancellationException

/**
 * The screenshots of the timeline do not outlive a launch: they are purged before the recorder can take one.
 * [run] never returns, it lasts as long as the app.
 */
internal class TimelineScreenshotsRunner(
    private val purgeTimelineScreenshotsUseCase: PurgeTimelineScreenshotsUseCase,
    private val timelineScreenshotRecorder: TimelineScreenshotRecorder,
) {

    suspend fun run() {
        try {
            purgeTimelineScreenshotsUseCase()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // a file that cannot be deleted (locked, ...) must not turn the feature off for the whole session
            e.printStackTrace()
        }
        timelineScreenshotRecorder.run()
    }
}
