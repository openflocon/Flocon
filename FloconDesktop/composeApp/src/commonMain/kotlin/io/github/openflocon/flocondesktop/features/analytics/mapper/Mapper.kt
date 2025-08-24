package io.github.openflocon.flocondesktop.features.analytics.mapper

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.network.list.mapper.formatTimestamp

internal const val MAX_PROPERTIES_TO_SHOW = 10

internal fun AnalyticsItemDomainModel.mapToUi(): AnalyticsRowUiModel = AnalyticsRowUiModel(
    id = itemId,
    dateFormatted = formatTimestamp(createdAt),
    eventName = eventName,
    properties = properties.map {
        AnalyticsRowUiModel.PropertyUiModel(
            name = it.name,
            value = it.value,
        )
    }.take(MAX_PROPERTIES_TO_SHOW),
    hasMoreProperties = properties.size > MAX_PROPERTIES_TO_SHOW,
)
