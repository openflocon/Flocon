@file:OptIn(ExperimentalSerializationApi::class)

package io.github.openflocon.data.local.deeplink.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import kotlinx.serialization.ExperimentalSerializationApi

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
        Index(value = ["deviceId", "name"], unique = true),
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
data class DeeplinkVariableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val name: String,
    val description: String?,
    val isHistory: Boolean
)

