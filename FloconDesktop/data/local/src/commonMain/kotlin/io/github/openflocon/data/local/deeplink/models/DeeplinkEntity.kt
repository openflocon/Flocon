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
) {

    @Serializable
    @JsonClassDiscriminator("type")
    sealed interface Parameter {
        val name: String

        @Serializable
        @SerialName("auto_complete")
        data class AutoComplete(
            override val name: String,
            val autoComplete: List<String>
        ) : Parameter

        @Serializable
        @SerialName("variable")
        data class Variable(
            override val name: String,
            val variableName: String
        ) : Parameter

    }
}

