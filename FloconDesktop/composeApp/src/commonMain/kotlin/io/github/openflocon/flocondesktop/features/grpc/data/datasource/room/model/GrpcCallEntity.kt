package io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["callId"], unique = true),
    ],
)
data class GrpcCallEntity(
    @PrimaryKey val callId: String, // GrpcCallId will be the primary key here
    val deviceId: String,
    @Embedded val request: GrpcCallEntity.Request,
) {
    data class Request(
        val timestamp: Long,
        val authority: String,
        val method: String,
        val data: String?,
        val headers: Map<String, String>,
    )
}
