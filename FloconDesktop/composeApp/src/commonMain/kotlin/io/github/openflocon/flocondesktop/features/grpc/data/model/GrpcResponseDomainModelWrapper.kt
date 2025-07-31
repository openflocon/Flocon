package io.github.openflocon.flocondesktop.features.grpc.data.model

import com.florent37.flocondesktop.features.grpc.domain.model.GrpcCallId
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel

data class GrpcResponseDomainModelWrapper(
    val callId: GrpcCallId,
    val response: GrpcResponseDomainModel,
)
