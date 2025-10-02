package io.github.openflocon.data.local.network.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlRequestEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlResponseEmbedded
import io.github.openflocon.data.local.network.models.grpc.NetworkCallGrpcResponseEmbedded
import io.github.openflocon.data.local.network.models.http.NetworkCallHttpResponseEmbedded

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
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
data class FloconNetworkCallEntity(
    @PrimaryKey
    val callId: String,

    val deviceId: String, // To associate with a device
    val packageName: String,
    val appInstance: Long, // the start time of the mobile app

    val type: FloconNetworkCallType,

    @Embedded(prefix = "request_")
    val request: FloconNetworkRequestEmbedded,

    @Embedded(prefix = "response_")
    val response: FloconNetworkResponseEmbedded?, // null if we did not received yet the response
)

enum class FloconNetworkCallType {
    HTTP,
    GRAPHQL,
    GRPC,
}

data class FloconNetworkRequestEmbedded(
    val url: String,
    val method: String,
    val startTime: Long,
    val startTimeFormatted: String,
    val byteSizeFormatted: String,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val requestByteSize: Long,
    val isMocked: Boolean,

    val domainFormatted: String, // for sorting & filtering
    val methodFormatted: String, // for sorting & filtering
    val queryFormatted: String, // for sorting & filtering

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlRequestEmbedded?,
)

data class FloconNetworkResponseEmbedded(
    val durationMs: Double,
    val durationFormatted: String,
    val responseContentType: String?,
    val responseBody: String?,
    val responseHeaders: Map<String, String>,
    val responseByteSize: Long,
    val responseByteSizeFormatted: String?,
    val responseError: String?,
    val isImage: Boolean,
    val status: String,

    @Embedded(prefix = "graphql_")
    val graphql: NetworkCallGraphQlResponseEmbedded?,

    @Embedded(prefix = "http_")
    val http: NetworkCallHttpResponseEmbedded?,

    @Embedded(prefix = "grpc_")
    val grpc: NetworkCallGrpcResponseEmbedded?,
)
