package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable

@Immutable
data class AnalyticsRowUiModel(
    val id: String,
    val dateFormatted: String,
    val eventName: String,
    val properties: List<PropertyUiModel>,
    val hasMoreProperties: Boolean,
    val isFromOldAppInstance: Boolean,
) {
    @Immutable
    data class PropertyUiModel(
        val name: String,
        val value: String,
    ) {
        fun contains(text: String): Boolean = value.contains(text, ignoreCase = true)
    }

    fun contains(text: String): Boolean = eventName.contains(text, ignoreCase = true) ||
        dateFormatted.contains(text, ignoreCase = true) ||
        properties.any { it.contains(text) }
}

fun previewAnalyticsRowUiModel(isFromOldAppInstance: Boolean = false) = AnalyticsRowUiModel(
    id = "id",
    dateFormatted = "2023-01-01 00:00:00",
    eventName = "event_name",
    properties = List(5) {
        AnalyticsRowUiModel.PropertyUiModel("param$it", "value$it")
    },
    hasMoreProperties = true,
    isFromOldAppInstance = isFromOldAppInstance,
)
