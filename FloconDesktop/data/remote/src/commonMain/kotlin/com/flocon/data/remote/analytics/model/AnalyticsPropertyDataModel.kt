package com.flocon.data.remote.analytics.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsPropertyDataModel(
    val name: String,
    val value: String,
)
