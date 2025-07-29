package com.florent37.flocondesktop.features.table.data.datasource.local.mapper

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.table.data.datasource.local.model.TableEntity
import com.florent37.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel

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
