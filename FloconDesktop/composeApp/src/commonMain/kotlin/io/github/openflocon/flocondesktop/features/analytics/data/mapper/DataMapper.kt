package io.github.openflocon.flocondesktop.features.analytics.data.mapper

import com.florent37.flocondesktop.features.analytics.data.model.AnalyticsItemDataModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsPropertyDomainModel

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
