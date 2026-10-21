package io.github.openflocon.flocondesktop.features.table.mapper

import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableDetailUiModel

fun TableIdentifierDomainModel.toUi() = DeviceTableUiModel(
    id = id,
    name = name,
)

fun TableDomainModel.toDetailUi(itemId: String): TableDetailUiModel? {
    val item = items.firstOrNull { it.itemId == itemId } ?: return null
    val columns = columns.zip(item.values) { column, value ->
        TableDetailUiModel.ColumnUiModel(name = column, value = value)
    }
    return TableDetailUiModel(
        tableName = name,
        title = columns.firstOrNull()?.value ?: name,
        dateFormatted = formatTimestamp(item.createdAt),
        columns = columns,
    )
}
