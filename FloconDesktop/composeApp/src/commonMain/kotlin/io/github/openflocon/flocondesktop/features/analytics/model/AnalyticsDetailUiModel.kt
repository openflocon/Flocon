package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable

@Immutable
data class AnalyticsDetailUiModel(
    val id: String,
    val dateFormatted: String,
    val eventName: String,
    val properties: List<PropertyUiModel>,
    val isFromOldAppInstance: Boolean,
) {
    @Immutable
    data class PropertyUiModel(
        val name: String,
        val value: String,
    )
}

fun previewAnalyticsDetailUiModel(isFromOldAppInstance: Boolean = false) = AnalyticsDetailUiModel(
    id = "id",
    dateFormatted = "2023-01-01 00:00:00",
    eventName = "event_name",
    properties = List(5) {
        AnalyticsDetailUiModel.PropertyUiModel("param$it", "value$it")
    },
    isFromOldAppInstance = isFromOldAppInstance,
)
