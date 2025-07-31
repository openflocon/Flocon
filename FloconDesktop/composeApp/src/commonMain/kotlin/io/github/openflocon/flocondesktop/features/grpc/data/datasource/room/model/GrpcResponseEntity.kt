package io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GrpcResponseEntity(
    @PrimaryKey @ColumnInfo(name = "response_call_id") val callId: String, // Linked to GrpcCallEntity's callId
    val responseTimestamp: Long,
    val status: String,
    val resultType: String, // "success" or "error"
    val resultData: String?, // Data for success, cause for error
    val headers: Map<String, String>,
)
