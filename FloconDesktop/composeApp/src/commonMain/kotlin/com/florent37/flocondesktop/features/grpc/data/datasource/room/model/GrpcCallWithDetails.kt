package com.florent37.flocondesktop.features.grpc.data.datasource.room.model

import androidx.room.Embedded
import androidx.room.Relation

data class GrpcCallWithDetails(
    @Embedded val call: GrpcCallEntity,
    @Relation(
        parentColumn = "callId",
        entityColumn = "response_call_id",
    )
    val response: GrpcResponseEntity?, // Response can be null if not yet received
)
