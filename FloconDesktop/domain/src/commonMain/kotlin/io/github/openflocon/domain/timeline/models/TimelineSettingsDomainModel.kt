package io.github.openflocon.domain.timeline.models

data class TimelineSettingsDomainModel(
    val autoScroll: Boolean = false, // jump to the newest event when a new one arrives
    val invertList: Boolean = false, // the newest event at the bottom instead of the top
    val pinPanel: Boolean = false, // the detail panel of an event stays next to the list instead of covering it
    val graph: Boolean = true, // the graph of the session instead of the list
    val mergePlugins: Boolean = false, // the graph packs every type in one set of rows instead of one lane per type
    val selectedSources: Set<TimelineSource> = TimelineSource.entries.toSet(), // the sources displayed: the chips at the top of the timeline
    val screenshots: Boolean = false, // a screenshot of the app is taken at a regular interval, to show next to the events
    val screenshotIntervalSeconds: Int = DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS,
)

val TimelineScreenshotIntervalsSeconds: List<Int> = listOf(5, 10, 15, 30)

const val DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS = 5
