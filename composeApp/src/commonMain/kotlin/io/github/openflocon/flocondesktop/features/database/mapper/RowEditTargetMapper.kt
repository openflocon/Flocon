package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseColumnAffinity
import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel

fun DatabaseRowEditTargetDomainModel.toUi() = DatabaseRowEditTargetUiModel(
    deviceId = deviceId,
    packageName = packageName,
    databaseId = databaseId,
    tableName = tableName,
    columns = columns.map {
        DatabaseRowEditTargetUiModel.Column(
            name = it.name,
            isPrimaryKey = it.isPrimaryKey,
            affinity = DatabaseRowEditTargetUiModel.Affinity.valueOf(it.affinity.name),
            isNullable = it.isNullable,
        )
    },
)

fun DatabaseRowEditTargetUiModel.toDomain() = DatabaseRowEditTargetDomainModel(
    deviceId = deviceId,
    packageName = packageName,
    databaseId = databaseId,
    tableName = tableName,
    columns = columns.map {
        DatabaseRowEditTargetDomainModel.Column(
            name = it.name,
            isPrimaryKey = it.isPrimaryKey,
            affinity = DatabaseColumnAffinity.valueOf(it.affinity.name),
            isNullable = it.isNullable,
        )
    },
)
