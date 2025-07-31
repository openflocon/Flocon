package io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
data class GrpcHeaderDataModel(
    val key: String,
    val value: String,
)
