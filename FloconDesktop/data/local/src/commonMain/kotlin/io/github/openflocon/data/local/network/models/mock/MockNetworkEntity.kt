package io.github.openflocon.data.local.network.models.mock

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["deviceId", "packageName"],
            childColumns = ["deviceId", "packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("deviceId", "packageName")
    ]
)
data class MockNetworkEntity(
    @PrimaryKey
    val mockId: String,
    val deviceId: String?,
    val packageName: String?,
    val isEnabled: Boolean,
    @Embedded(prefix = "expectation_")
    val expectation: MockNetworkExpectationEmbedded,
    val response: String, // saved as json
)

data class MockNetworkExpectationEmbedded(
    val urlPattern: String,
    val method: String,
)
