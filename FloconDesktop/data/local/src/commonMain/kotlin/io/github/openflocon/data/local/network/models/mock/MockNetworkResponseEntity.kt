package io.github.openflocon.data.local.network.models.mock

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("deviceId", "packageName")
    ]
)
data class MockNetworkResponseEntity(
    @PrimaryKey
    val mockId: String,
    val deviceId: String,
    val packageName: String,
    @Embedded(prefix = "expectation_")
    val expectation: MockNetworkExpectationEmbedded,
    @Embedded(prefix = "response_")
    val response: MockNetworkResponseEmbedded,
)

data class MockNetworkExpectationEmbedded(
    val urlPattern: String,
    val method: String,
)

data class MockNetworkResponseEmbedded(
    val httpCode: Int,
    val body: String,
    val mediaType: String,
    val delay: Long,
    val headers: Map<String, String>,
)
