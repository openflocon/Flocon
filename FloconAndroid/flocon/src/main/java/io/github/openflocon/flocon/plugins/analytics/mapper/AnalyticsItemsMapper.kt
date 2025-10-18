package io.github.openflocon.flocon.plugins.analytics.mapper

import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun analyticsItemsToJson(json: Json, item: AnalyticsItem): String {
    return json.encodeToString(
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