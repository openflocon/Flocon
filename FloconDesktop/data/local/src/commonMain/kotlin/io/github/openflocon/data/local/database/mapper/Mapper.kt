package io.github.openflocon.data.local.database.mapper

import io.github.openflocon.data.local.database.models.DabataseTableEntity
import io.github.openflocon.data.local.database.models.DabataseTableEntityColumn
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json


internal fun DatabaseTableDomainModel.toEntity(
    json: Json,
    databaseId: DeviceDataBaseId,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
): DabataseTableEntity {
    val columns = columns.map { it.toEntity() }
    return DabataseTableEntity(
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


internal fun DabataseTableEntity.toDomain(
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
)

fun DabataseTableEntityColumn.toDomain() = DatabaseTableDomainModel.Column(
    name = name,
    type = type,
)

