package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkRequestEmbedded
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlRequestEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlResponseEmbedded
import io.github.openflocon.data.local.network.models.grpc.NetworkCallGrpcResponseEmbedded
import io.github.openflocon.data.local.network.models.http.NetworkCallHttpResponseEmbedded
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

fun FloconNetworkCallDomainModel.toEntity(
    deviceId: DeviceId,
    packageName: String
): FloconNetworkCallEntity {
    return FloconNetworkCallEntity(
        callId = callId,
        deviceId = deviceId,
        packageName = packageName,
        type = when (this) {
            is FloconNetworkCallDomainModel.Http -> FloconNetworkCallType.HTTP
            is FloconNetworkCallDomainModel.GraphQl -> FloconNetworkCallType.GRAPHQL
            is FloconNetworkCallDomainModel.Grpc -> FloconNetworkCallType.GRPC
        },
        request = FloconNetworkRequestEmbedded(
            url = networkRequest.url,
            method = networkRequest.method,
            startTime = networkRequest.startTime,
            requestHeaders = networkRequest.headers,
            requestBody = networkRequest.body,
            requestByteSize = networkRequest.byteSize,
            graphql = when (this) {
                is FloconNetworkCallDomainModel.GraphQl -> NetworkCallGraphQlRequestEmbedded(
                    query = this.request.query,
                    operationType = this.request.operationType,
                )

                else -> null
            }
        ),
        response = networkResponse?.let { networkResponse ->
            FloconNetworkResponseEmbedded(
                durationMs = networkResponse.durationMs,
                responseContentType = networkResponse.contentType,
                responseBody = networkResponse.body,
                responseHeaders = networkResponse.headers,
                responseByteSize = networkResponse.byteSize,
                graphql = when (this) {
                    is FloconNetworkCallDomainModel.GraphQl -> NetworkCallGraphQlResponseEmbedded(
                        responseHttpCode = (this.response as FloconNetworkCallDomainModel.GraphQl.Response).httpCode,
                        isSuccess = (this.response as FloconNetworkCallDomainModel.GraphQl.Response).isSuccess,
                    )

                    is FloconNetworkCallDomainModel.Grpc,
                    is FloconNetworkCallDomainModel.Http -> null
                },
                http = when (this) {
                    is FloconNetworkCallDomainModel.Http -> NetworkCallHttpResponseEmbedded(
                        responseHttpCode = (this.response as FloconNetworkCallDomainModel.Http.Response).httpCode,
                    )

                    is FloconNetworkCallDomainModel.Grpc,
                    is FloconNetworkCallDomainModel.GraphQl -> null
                },
                grpc = when (this) {
                    is FloconNetworkCallDomainModel.Grpc -> NetworkCallGrpcResponseEmbedded(
                        responseStatus = (this.response as FloconNetworkCallDomainModel.Grpc.Response).responseStatus,
                    )

                    is FloconNetworkCallDomainModel.Http,
                    is FloconNetworkCallDomainModel.GraphQl -> null
                }
            )
        }
    )
}
