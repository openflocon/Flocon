package io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

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
