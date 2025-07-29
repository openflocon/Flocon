package com.florent37.flocondesktop.features.grpc.domain.model

data class GrpcCallDomainModel(
    val id: String,
    val request: GrpcRequestDomainModel,
    val response: GrpcResponseDomainModel?,
)
