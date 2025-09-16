package io.github.openflocon.data.local.deeplink.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import kotlinx.serialization.Serializable

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
        Index(value = ["deviceId", "link"], unique = true),
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
data class DeeplinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val link: String,
    val label: String?,
    val description: String?,
    val parametersAsJson: String,
    val isHistory: Boolean,
)

@Serializable
data class DeeplinkParameterEntity(
    val paramName: String,
    val autoComplete: List<String>,
)
