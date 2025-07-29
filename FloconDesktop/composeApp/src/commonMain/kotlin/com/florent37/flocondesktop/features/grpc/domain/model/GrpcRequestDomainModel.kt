package com.florent37.flocondesktop.features.grpc.domain.model

data class GrpcRequestDomainModel(
    val timestamp: Long,
    val authority: String,
    val method: String,
    val headers: List<GrpcHeaderDomainModel>,
    val data: String?,
)
