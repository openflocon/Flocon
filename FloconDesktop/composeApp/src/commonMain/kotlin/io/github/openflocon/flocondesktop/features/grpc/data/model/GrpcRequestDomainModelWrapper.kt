package io.github.openflocon.flocondesktop.features.grpc.data.model

import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel

data class GrpcRequestDomainModelWrapper(
    val callId: GrpcCallId,
    val request: GrpcRequestDomainModel,
)
