package io.github.openflocon.data.local.database.mapper

import io.github.openflocon.data.local.database.models.DabataseTableEntityColumn
import io.github.openflocon.data.local.database.models.DatabaseTableEntity
import io.github.openflocon.data.local.database.models.FavoriteQueryEntity
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json


internal fun DatabaseTableDomainModel.toEntity(
    json: Json,
    databaseId: DeviceDataBaseId,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
): DatabaseTableEntity {
    val columns = columns.map { it.toEntity() }
    return DatabaseTableEntity(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        databaseId = databaseId,
        tableName = tableName,
        columnsAsString = try {
            json.encodeToString(columns)
        } catch (t: Throwable) {
            t.printStackTrace()
            "[]"
        }
    )
}


internal fun DatabaseTableEntity.toDomain(
    json: Json,
): DatabaseTableDomainModel {
    val columnsEntity = try {
        json.decodeFromString<List<DabataseTableEntityColumn>>(columnsAsString)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
    return DatabaseTableDomainModel(
        tableName = tableName,
        columns = columnsEntity.map { it.toDomain() }
    )
}

fun DatabaseTableDomainModel.Column.toEntity() = DabataseTableEntityColumn(
    name = name,
    type = type,
    nullable = nullable,
    primaryKey = primaryKey,
)

fun DabataseTableEntityColumn.toDomain() = DatabaseTableDomainModel.Column(
    name = name,
    type = type,
    nullable = nullable,
    primaryKey = primaryKey,
)

fun FavoriteQueryEntity.toDomain() = DatabaseFavoriteQueryDomainModel(
    id = id,
    databaseId = databaseId,
    title = title,
    query = queryString,
)