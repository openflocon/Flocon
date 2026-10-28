package io.github.openflocon.domain.timeline.models

data class TimelineSettingsDomainModel(
    val autoScroll: Boolean = false, // jump to the newest event when a new one arrives
    val invertList: Boolean = false, // the newest event at the bottom instead of the top
    val pinPanel: Boolean = false, // the detail panel of an event stays next to the list instead of covering it
    val selectedSources: Set<TimelineSource> = TimelineSource.entries.toSet(), // the sources displayed: the chips at the top of the timeline
)
