package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel

fun DeviceDataBaseDomainModel.toUi(selected: DatabaseAndTablesDomainModel): DeviceDataBaseUiModel {
    val isSelected = this.id == selected.database.id
    return DeviceDataBaseUiModel(
        id = this.id,
        name = this.name,
        isSelected = isSelected,
        tables = if(isSelected) {
            selected.tables.map {
                it.toUi()
            }
        } else null
    )
}

fun DatabaseTableDomainModel.toUi() : TableUiModel {
    return TableUiModel(
        name = this.tableName,
        columns = this.columns.map {
            TableUiModel.ColumnUiModel(
                name = it.name,
                type = it.type,
            )
        }
    )
}
