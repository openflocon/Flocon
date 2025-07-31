package io.github.openflocon.flocondesktop.features.grpc.data.model

import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallId
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel

data class GrpcResponseDomainModelWrapper(
    val callId: GrpcCallId,
    val response: GrpcResponseDomainModel,
)
