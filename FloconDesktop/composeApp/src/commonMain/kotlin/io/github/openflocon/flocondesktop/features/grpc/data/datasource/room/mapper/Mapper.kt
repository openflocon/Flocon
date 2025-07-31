package io.github.openflocon.flocondesktop.features.grpc.data.datasource.room

import io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallWithDetails
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcCallDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcHeaderDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel

fun GrpcCallWithDetails.toDomainModel(): GrpcCallDomainModel {
    val requestDomain = with(call.request) {
        GrpcRequestDomainModel(
            timestamp = timestamp,
            authority = authority,
            method = method,
            headers = headers
                .map { GrpcHeaderDomainModel(key = it.key, value = it.value) },
            data = data,
        )
    }

    val responseDomain = response?.let {
        val callResult = when (it.resultType) {
            "success" -> GrpcResponseDomainModel.CallResult.Success(it.resultData ?: "")
            "error" -> GrpcResponseDomainModel.CallResult.Error(
                it.resultData ?: "Unknown error",
            )

            else -> error("Unknown result type")
        }
        GrpcResponseDomainModel(
            timestamp = it.responseTimestamp,
            status = it.status,
            headers = response.headers
                .map { header ->
                    GrpcHeaderDomainModel(
                        key = header.key,
                        value = header.value,
                    )
                },
            result = callResult,
        )
    }

    return GrpcCallDomainModel(
        id = call.callId,
        request = requestDomain,
        response = responseDomain,
    )
}
