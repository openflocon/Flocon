package io.github.openflocon.domain.timeline.models

// when the events happened, the time range being cut in [bucketCount] slices
data class TimelineActivityDomainModel(
    val bucketCount: Int,
    // number of events of each source in each slice that has some
    val sourceBuckets: Map<TimelineSource, Map<Int, Int>>,
    // the slices where an app starts
    val sessionBuckets: Set<Int>,
) {
    companion object {
        fun empty(bucketCount: Int) = TimelineActivityDomainModel(
            bucketCount = bucketCount,
            sourceBuckets = emptyMap(),
            sessionBuckets = emptySet(),
        )
    }
}
