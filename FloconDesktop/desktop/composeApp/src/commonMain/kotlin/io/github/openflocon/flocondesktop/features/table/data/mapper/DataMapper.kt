package io.github.openflocon.flocondesktop.features.table.data.mapper

import io.github.openflocon.flocondesktop.features.table.data.model.TableItemDataModel
import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel

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
