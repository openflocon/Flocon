@file:OptIn(ExperimentalSerializationApi::class)

package io.github.openflocon.data.local.deeplink.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

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
    val isHistory: Boolean,
    val mode: Mode = Mode.Input
) {

    @Serializable
    @JsonClassDiscriminator("type")
    sealed interface Mode {

        @Serializable
        @SerialName("input")
        object Input : Mode

        @Serializable
        @SerialName("auto_complete")
        data class AutoComplete(val suggestions: List<String>) : Mode

    }

}

