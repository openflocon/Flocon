package com.florent37.flocondesktop.features.grpc.data.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
data class GrpcRequestDataModel(
    val id: String,
    val timestamp: Long,
    val authority: String,
    val method: String,
    val headers: List<GrpcHeaderDataModel>,
    val data: String? = null,
)
