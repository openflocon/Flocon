package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
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
                isPrimaryKey = it.primaryKey,
                type = buildString {
                    append(it.type)
                    if(!it.nullable) {
                        append(", NOT NULL")
                    }
                },
            )
        }
    )
}

fun DatabaseExecuteSqlResponseDomainModel.toUi(): QueryResultUiModel = when (this) {
    is DatabaseExecuteSqlResponseDomainModel.Error -> QueryResultUiModel.Text(this.message)
    is DatabaseExecuteSqlResponseDomainModel.Insert -> QueryResultUiModel.Text("Inserted (insertedId=$insertedId)")
    DatabaseExecuteSqlResponseDomainModel.RawSuccess -> QueryResultUiModel.Text("Success")
    is DatabaseExecuteSqlResponseDomainModel.Select ->
        QueryResultUiModel.Values(
            columns = this.columns,
            rows =
                values.map {
                    DatabaseRowUiModel(it)
                },
        )

    is DatabaseExecuteSqlResponseDomainModel.UpdateDelete -> QueryResultUiModel.Text("Done, affected=$affectedCount")
}

internal fun DatabaseFavoriteQueryDomainModel.mapToUi() : DatabaseFavoriteQueryUiModel {
    return DatabaseFavoriteQueryUiModel(
        id = id,
        databaseId = databaseId,
        title = title,
    )
}