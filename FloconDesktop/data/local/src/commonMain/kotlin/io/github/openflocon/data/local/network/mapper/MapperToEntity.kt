package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.FloconNetworkCallType
import io.github.openflocon.data.local.network.models.FloconNetworkRequestEmbedded
import io.github.openflocon.data.local.network.models.FloconNetworkResponseEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlRequestEmbedded
import io.github.openflocon.data.local.network.models.graphql.NetworkCallGraphQlResponseEmbedded
import io.github.openflocon.data.local.network.models.grpc.NetworkCallGrpcResponseEmbedded
import io.github.openflocon.data.local.network.models.http.NetworkCallHttpResponseEmbedded
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

fun FloconNetworkCallDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
): FloconNetworkCallEntity {
    return FloconNetworkCallEntity(
        callId = callId,
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        appInstance = deviceIdAndPackageName.appInstance,
        type = when (this.request.specificInfos) {
            is FloconNetworkCallDomainModel.Request.SpecificInfos.Http -> FloconNetworkCallType.HTTP
            is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> FloconNetworkCallType.GRAPHQL
            is FloconNetworkCallDomainModel.Request.SpecificInfos.Grpc -> FloconNetworkCallType.GRPC
        },
        request = FloconNetworkRequestEmbedded(
            url = request.url,
            method = request.method,
            startTime = request.startTime,
            requestHeaders = request.headers,
            requestBody = request.body,
            requestByteSize = request.byteSize,
            isMocked = request.isMocked,
            graphql = when (val s = this.request.specificInfos) {
                is FloconNetworkCallDomainModel.Request.SpecificInfos.GraphQl -> NetworkCallGraphQlRequestEmbedded(
                    query = s.query,
                    operationType = s.operationType,
                )

                else -> null
            }
        ),
        response = response?.let { networkResponse ->
            when(networkResponse) {
                is FloconNetworkCallDomainModel.Response.Failure -> {
                    FloconNetworkResponseEmbedded(
                        durationMs = networkResponse.durationMs,
                        responseError = networkResponse.issue,
                        graphql = null,
                        http = null,
                        grpc = null,
                        responseContentType = null,
                        responseBody = null,
                        responseHeaders = emptyMap(),
                        responseByteSize = 0,
                        isImage = false,
                    )
                }
                is FloconNetworkCallDomainModel.Response.Success -> {
                    FloconNetworkResponseEmbedded(
                        durationMs = networkResponse.durationMs,
                        responseContentType = networkResponse.contentType,
                        responseBody = networkResponse.body,
                        responseHeaders = networkResponse.headers,
                        responseByteSize = networkResponse.byteSize,
                        responseError = null,
                        isImage = networkResponse.isImage,
                        graphql = when (val s = networkResponse.specificInfos) {
                            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.GraphQl -> NetworkCallGraphQlResponseEmbedded(
                                responseHttpCode = s.httpCode,
                                isSuccess = s.isSuccess,
                            )

                            else -> null
                        },
                        http = when (val s = networkResponse.specificInfos) {
                            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http -> NetworkCallHttpResponseEmbedded(
                                responseHttpCode = s.httpCode,
                            )

                            else -> null
                        },
                        grpc = when (val s = networkResponse.specificInfos) {
                            is FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Grpc -> NetworkCallGrpcResponseEmbedded(
                                responseStatus = s.grpcStatus,
                            )
                            else -> null
                        }
                    )
                }
            }
        }
    )
}
