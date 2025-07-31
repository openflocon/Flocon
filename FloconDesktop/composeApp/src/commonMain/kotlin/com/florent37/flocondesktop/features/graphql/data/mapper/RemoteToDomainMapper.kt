package com.florent37.flocondesktop.features.graphql.data.mapper

import com.florent37.flocondesktop.features.grpc.data.model.GrpcRequestDomainModelWrapper
import com.florent37.flocondesktop.features.grpc.data.model.GrpcResponseDomainModelWrapper
import com.florent37.flocondesktop.features.grpc.data.model.fromdevice.GrpcHeaderDataModel
import com.florent37.flocondesktop.features.grpc.data.model.fromdevice.GrpcRequestDataModel
import com.florent37.flocondesktop.features.grpc.data.model.fromdevice.GrpcResponseDataModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcHeaderDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcRequestDomainModel
import com.florent37.flocondesktop.features.grpc.domain.model.GrpcResponseDomainModel

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
