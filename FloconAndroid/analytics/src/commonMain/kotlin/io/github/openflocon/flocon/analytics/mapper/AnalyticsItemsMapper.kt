package io.github.openflocon.flocon.analytics.mapper

import io.github.openflocon.flocon.analytics.model.AnalyticsItem
import kotlinx.serialization.Serializable

@Serializable
internal class AnalyticsItemSerializable(
    val id: String,
    val analyticsTableId: String,
    val eventName: String,
    val createdAt: Long,
    val properties: List<AnalyticsPropertySerializable>,
)

@Serializable
internal class AnalyticsPropertySerializable(
    val name: String,
    val value: String,
)

internal fun AnalyticsItem.toRemote(): AnalyticsItemSerializable {
    return AnalyticsItemSerializable(
        id = id,
        analyticsTableId = analyticsTableId,
        eventName = eventName,
        createdAt = createdAt,
        properties = properties.map {
            AnalyticsPropertySerializable(
                name = it.name,
                value = it.value
            )
        }
    )
}
