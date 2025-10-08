package io.github.openflocon.data.local.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import kotlinx.serialization.Serializable

@Entity(
    primaryKeys = [
                  "deviceId",
                  "packageName",
                  "databaseId",
                  "tableName"
    ],
    indices = [
        Index(value = ["databaseId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["deviceId", "packageName"],
            childColumns = ["deviceId", "packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class DabataseTableEntity(
    val deviceId: String,
    val packageName: String,
    val databaseId: String,
    val tableName: String,
    val columnsAsString: String, // json formatted of List<DabataseTableEntityColumn>
)

@Serializable
data class DabataseTableEntityColumn(
    val name: String,
    val type: String,
)
