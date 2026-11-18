package com.flocon.data.remote.timelinelog.model

import kotlinx.serialization.Serializable

@Serializable
data class TimelineLogDataModel(
    val type: String,
    val value: String,
    val params: Map<String, String> = emptyMap(), // absent when empty
    val level: String? = null, // absent for typed logs
    val timestamp: Long,
)
