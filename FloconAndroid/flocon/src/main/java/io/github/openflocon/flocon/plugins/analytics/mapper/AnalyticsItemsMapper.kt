package io.github.openflocon.flocon.plugins.analytics.mapper

import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsItem
import org.json.JSONArray
import org.json.JSONObject

internal fun analyticsItemsToJson(item: AnalyticsItem) : JSONArray {
    val array = JSONArray()
    // Flocon server is expecing an array of json elements

    array.put(item.toJson())

    return array
}

private fun AnalyticsItem.toJson() : JSONObject {
    val analyticsItemJson = JSONObject()

    analyticsItemJson.put("id", this.id)
    analyticsItemJson.put("analyticsTableId", this.analyticsTableId)
    analyticsItemJson.put("eventName", this.eventName)
    analyticsItemJson.put("createdAt", this.createdAt)

    val array = JSONArray()
    properties.forEach {
        array.put(
            JSONObject().apply {
                put("name", it.name)
                put("value", it.value)
            }
        )
    }

    analyticsItemJson.put("properties", array)
    return analyticsItemJson
}