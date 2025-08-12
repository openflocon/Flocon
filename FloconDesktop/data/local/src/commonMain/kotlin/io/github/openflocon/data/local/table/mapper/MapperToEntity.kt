package io.github.openflocon.data.local.table.mapper

import io.github.openflocon.data.local.table.models.TableEntity
import io.github.openflocon.data.local.table.models.TableItemEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel

internal fun TableDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
): TableEntity = TableEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    name = name,
)

internal fun TableDomainModel.TableItem.toEntity(
    tableId: Long,
): TableItemEntity = TableItemEntity(
    tableId = tableId,
    createdAt = createdAt,
    values = values,
    columnsNames = columns,
    itemId = itemId,
)
