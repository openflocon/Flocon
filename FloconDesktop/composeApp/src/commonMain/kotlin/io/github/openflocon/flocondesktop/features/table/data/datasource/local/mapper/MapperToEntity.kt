package io.github.openflocon.flocondesktop.features.table.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel

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
