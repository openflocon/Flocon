package io.github.openflocon.flocondesktop.features.graphql.data.mapper

import io.github.openflocon.flocondesktop.features.grpc.data.model.GrpcRequestDomainModelWrapper
import io.github.openflocon.flocondesktop.features.grpc.data.model.GrpcResponseDomainModelWrapper
import io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice.GrpcHeaderDataModel
import io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice.GrpcRequestDataModel
import io.github.openflocon.flocondesktop.features.grpc.data.model.fromdevice.GrpcResponseDataModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcHeaderDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import io.github.openflocon.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel

fun toDomain(dataModel: GrpcRequestDataModel): GrpcRequestDomainModelWrapper? = GrpcRequestDomainModelWrapper(
    callId = dataModel.id,
    request = GrpcRequestDomainModel(
        timestamp = dataModel.timestamp,
        authority = dataModel.authority,
        method = dataModel.method,
        headers = dataModel.headers.map {
            toDomain(it)
        },
        data = dataModel.data,
    ),
)

fun toDomain(dataModel: GrpcResponseDataModel): GrpcResponseDomainModelWrapper? {
    return GrpcResponseDomainModelWrapper(
        callId = dataModel.id,
        response = GrpcResponseDomainModel(
            timestamp = dataModel.timestamp,
            status = dataModel.status,
            headers = dataModel.headers.map {
                toDomain(it)
            },
            result = when {
                dataModel.data != null -> GrpcResponseDomainModel.CallResult.Success(
                    data = dataModel.data,
                )
                dataModel.cause != null -> GrpcResponseDomainModel.CallResult.Error(
                    cause = dataModel.cause,
                )
                else -> return null
            },
        ),
    )
}

private fun toDomain(model: GrpcHeaderDataModel): GrpcHeaderDomainModel = GrpcHeaderDomainModel(
    key = model.key,
    value = model.value,
)
