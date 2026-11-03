package io.github.openflocon.flocondesktop.app

import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRecorder
import io.github.openflocon.domain.timeline.usecase.PurgeTimelineScreenshotsUseCase

/**
 * The screenshots of the timeline do not outlive a launch: they are purged before the recorder can take one.
 * [run] never returns, it lasts as long as the app.
 */
internal class TimelineScreenshotsRunner(
    private val purgeTimelineScreenshotsUseCase: PurgeTimelineScreenshotsUseCase,
    private val timelineScreenshotRecorder: TimelineScreenshotRecorder,
) {

    suspend fun run() {
        purgeTimelineScreenshotsUseCase()
        timelineScreenshotRecorder.run()
    }
}
