package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel

fun toAnalyticsDomain(entity: AnalyticsItemEntity): AnalyticsItemDomainModel = AnalyticsItemDomainModel(
    analyticsTableId = entity.analyticsTableId,
    itemId = entity.itemId,
    createdAt = entity.createdAt,
    eventName = entity.eventName,
    properties = entity.propertiesValues.mapIndexedNotNull { index, value ->
        AnalyticsPropertyDomainModel(
            name = entity.propertiesColumnsNames.getOrNull(index)
                ?: return@mapIndexedNotNull null,
            value = value,
        )
    },
)
