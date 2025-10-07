package com.flocon.data.remote.analytics.mapper

import com.flocon.data.remote.analytics.model.AnalyticsItemDataModel
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.analytics.models.AnalyticsPropertyDomainModel
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.device.models.AppInstance

internal fun AnalyticsItemDataModel.toDomain(
    appInstance: AppInstance,
) = AnalyticsItemDomainModel(
    analyticsTableId = analyticsTableId,
    itemId = id,
    createdAt = createdAt,
    createdAtFormatted = formatTimestamp(createdAt),
    eventName = eventName,
    properties = properties?.map {
        AnalyticsPropertyDomainModel(
            name = it.name,
            value = it.value,
        )
    } ?: emptyList(),
    appInstance = appInstance,
)
