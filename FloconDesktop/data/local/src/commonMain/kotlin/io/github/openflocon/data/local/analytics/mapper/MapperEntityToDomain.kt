package io.github.openflocon.data.local.analytics.mapper

import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel

fun AnalyticsItemEntity.toAnalyticsDomain(): AnalyticsItemDomainModel = AnalyticsItemDomainModel(
    analyticsTableId = analyticsTableId,
    itemId = itemId,
    createdAt = createdAt,
    eventName = eventName,
    properties = propertiesValues.mapIndexedNotNull { index, value ->
        AnalyticsPropertyDomainModel(
            name = propertiesColumnsNames.getOrNull(index)
                ?: return@mapIndexedNotNull null,
            value = value,
        )
    },
    appInstance = appInstance,
)
