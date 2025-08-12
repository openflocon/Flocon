package com.flocon.data.remote.analytics.mapper

import com.flocon.data.remote.analytics.model.AnalyticsItemDataModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel

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
