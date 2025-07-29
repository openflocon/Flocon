package com.florent37.flocondesktop.features.grpc.data.model

import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel

data class GrpcRequestDomainModelWrapper(
    val callId: GrpcCallId,
    val request: GrpcRequestDomainModel,
)
