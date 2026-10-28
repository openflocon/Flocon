package io.github.openflocon.domain.timeline.models

data class TimelineSettingsDomainModel(
    val autoScroll: Boolean = false, // jump to the newest event when a new one arrives
    val invertList: Boolean = false, // the newest event at the bottom instead of the top
)
