package io.github.openflocon.flocon.analytics.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.uuid.Uuid

@Serializable
internal data class AnalyticsItem(
    @SerialName("id")
    val id: String,
    @SerialName("analytics_table_id")
    val analyticsTableId: String,
    @SerialName("event_name")
    val eventName: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("properties")
    val properties: List<AnalyticsPropertiesConfig>,
)

internal fun AnalyticsEvent.toItem() = AnalyticsItem(
    id = Uuid.random().toString(),
    analyticsTableId = analyticsTableId,
    eventName = eventName,
    createdAt = Clock.System.now().toEpochMilliseconds(),
    properties = properties
)