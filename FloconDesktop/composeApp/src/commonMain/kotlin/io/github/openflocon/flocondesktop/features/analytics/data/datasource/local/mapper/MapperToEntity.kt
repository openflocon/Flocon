package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import com.florent37.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel

internal fun AnalyticsItemDomainModel.toEntity(
    deviceId: DeviceId,
) = AnalyticsItemEntity(
    analyticsTableId = analyticsTableId,
    createdAt = createdAt,
    eventName = eventName,
    itemId = itemId,
    deviceId = deviceId,
    propertiesValues = properties.map { it.value },
    propertiesColumnsNames = properties.map { it.name },
)
