package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper

import com.florent37.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsPropertyDomainModel

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
