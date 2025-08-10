package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import io.github.openflocon.flocondesktop.features.analytics.domain.model.AnalyticsItemDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel

internal fun AnalyticsItemDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
) = AnalyticsItemEntity(
    analyticsTableId = analyticsTableId,
    createdAt = createdAt,
    eventName = eventName,
    itemId = itemId,
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    propertiesValues = properties.map { it.value },
    propertiesColumnsNames = properties.map { it.name },
)
