package io.github.openflocon.flocon.plugins.analytics.mapper

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

internal fun analyticsItemsToJson(item: AnalyticsItem): String {
    return FloconEncoder.json.encodeToString(
        listOf(
            item.toSerializable()
        )
    )
}

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

internal fun AnalyticsItem.toSerializable(): AnalyticsItemSerializable {
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