package com.flocon.data.remote.crashreporter.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CrashReportDataModel(
    val crashId: String,
    val timestamp: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)
