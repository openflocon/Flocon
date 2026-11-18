package com.flocon.data.remote.table.mapper

import com.flocon.data.remote.table.model.TableItemDataModel
import io.github.openflocon.domain.table.models.TableDomainModel

internal fun toDomain(dataModel: TableItemDataModel): TableDomainModel = TableDomainModel(
    name = dataModel.name,
    columns = dataModel.columns.map { it.column },
    items = listOf(
        TableDomainModel.TableItem(
            itemId = dataModel.id,
            createdAt = dataModel.createdAt,
            values = dataModel.columns.map { it.value },
        ),
    ),
)
