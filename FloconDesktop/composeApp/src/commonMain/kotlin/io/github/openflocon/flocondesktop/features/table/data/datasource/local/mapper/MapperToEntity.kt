package io.github.openflocon.flocondesktop.features.table.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel

internal fun TableDomainModel.toEntity(
    deviceId: DeviceId,
): TableEntity = TableEntity(
    deviceId = deviceId,
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
