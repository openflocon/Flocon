package com.florent37.flocondesktop.features.analytics.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsPropertyDataModel(
    val name: String,
    val value: String,
)
