package com.florent37.flocondesktop.features.table.data.mapper

import com.florent37.flocondesktop.features.table.data.model.TableItemDataModel
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel

internal fun toDomain(dataModel: TableItemDataModel): TableDomainModel = TableDomainModel(
    name = dataModel.name,
    items = listOf(
        TableDomainModel.TableItem(
            itemId = dataModel.id,
            createdAt = dataModel.createdAt,
            values = dataModel.columns.map { it.value },
            columns = dataModel.columns.map { it.column },
        ),
    ),
)
