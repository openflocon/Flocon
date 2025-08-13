package io.github.openflocon.flocondesktop.features.analytics.data.mapper

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel
import io.github.openflocon.flocondesktop.features.analytics.data.model.AnalyticsItemDataModel

internal fun toDomain(dataModel: AnalyticsItemDataModel) = AnalyticsItemDomainModel(
    analyticsTableId = dataModel.analyticsTableId,
    itemId = dataModel.id,
    createdAt = dataModel.createdAt,
    eventName = dataModel.eventName,
    properties = dataModel.properties?.map {
        AnalyticsPropertyDomainModel(
            name = it.name,
            value = it.value,
        )
    } ?: emptyList(),
)
