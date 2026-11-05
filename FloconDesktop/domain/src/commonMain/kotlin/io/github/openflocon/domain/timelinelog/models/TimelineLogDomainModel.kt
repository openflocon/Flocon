package io.github.openflocon.domain.timelinelog.models

data class TimelineLogDomainModel(
    val id: Long, // 0 until the log is stored
    val timestamp: Long,
    val type: String,
    val level: TimelineLogLevel,
    val value: String,
    val params: Map<String, String>,
)
