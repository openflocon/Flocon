package com.florent37.flocondesktop.features.grpc.data.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
data class GrpcResponseDataModel(
    val id: String,
    val timestamp: Long,
    val status: String,
    val cause: String? = null,
    val headers: List<GrpcHeaderDataModel>,
    val data: String? = null,
)
