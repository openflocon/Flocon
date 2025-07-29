package io.github.openflocon.flocon.plugins.analytics.model

import org.json.JSONArray
import org.json.JSONObject

data class AnalyticsItem(
    val id: String,
    val analyticsTableId: String,
    val eventName: String,
    val createdAt: Long,
    val properties: List<AnalyticsPropertiesConfig>,
) {
    companion object {
        fun listToJson(items: Collection<AnalyticsItem>) : JSONArray {
            val array = JSONArray()

            items.forEach {
                array.put(it.toJson())
            }

            return array
        }
    }
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