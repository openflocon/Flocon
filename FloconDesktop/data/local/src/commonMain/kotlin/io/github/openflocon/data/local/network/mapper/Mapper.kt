package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FloconHttpRequestEntity
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityLite
import io.github.openflocon.data.local.network.models.FloconHttpRequestInfosEntity
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityGraphQlEmbedded
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityGrpcEmbedded
import io.github.openflocon.data.local.network.models.FloconHttpRequestEntityHttpEmbedded
import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel

fun FloconHttpRequestDomainModel.toEntity(
    deviceId: String,
    packageName: String,
): FloconHttpRequestEntity = FloconHttpRequestEntity(
    uuid = this.uuid,
    infos = this.toInfosEntity(),
    deviceId = deviceId,
    http = when (val t = this.type) {
        is FloconHttpRequestDomainModel.Type.Http -> FloconHttpRequestEntityHttpEmbedded(
            responseHttpCode = t.httpCode,
        )

        is FloconHttpRequestDomainModel.Type.GraphQl,
        is FloconHttpRequestDomainModel.Type.Grpc,
            -> null
    },
    graphql = when (val t = this.type) {
        is FloconHttpRequestDomainModel.Type.GraphQl -> FloconHttpRequestEntityGraphQlEmbedded(
            query = t.query,
            operationType = t.operationType,
            isSuccess = t.isSuccess,
            responseHttpCode = t.httpCode,
        )

        is FloconHttpRequestDomainModel.Type.Http,
        is FloconHttpRequestDomainModel.Type.Grpc,
            -> null
    },
    grpc = when (val t = this.type) {
        is FloconHttpRequestDomainModel.Type.Grpc -> FloconHttpRequestEntityGrpcEmbedded(
            responseStatus = t.responseStatus,
        )

        is FloconHttpRequestDomainModel.Type.Http,
        is FloconHttpRequestDomainModel.Type.GraphQl,
            -> null
    },
    packageName = packageName,
)

private fun FloconHttpRequestDomainModel.toInfosEntity(): FloconHttpRequestInfosEntity = FloconHttpRequestInfosEntity(
    url = this.url,
    method = this.request.method,
    startTime = this.request.startTime,
    durationMs = this.durationMs,
    requestHeaders = this.request.headers,
    requestBody = this.request.body,
    requestByteSize = this.request.byteSize,
    responseContentType = this.response.contentType,
    responseBody = this.response.body,
    responseHeaders = this.response.headers,
    responseByteSize = this.response.byteSize,
)

fun FloconHttpRequestEntity.toDomainModel(): FloconHttpRequestDomainModel? {
    return FloconHttpRequestDomainModel(
        uuid = this.uuid,
        url = this.infos.url,
        durationMs = this.infos.durationMs,
        request = FloconHttpRequestDomainModel.Request(
            method = this.infos.method,
            startTime = this.infos.startTime,
            headers = this.infos.requestHeaders,
            body = this.infos.requestBody,
            byteSize = this.infos.requestByteSize,
        ),
        response = FloconHttpRequestDomainModel.Response(
            contentType = this.infos.responseContentType,
            body = this.infos.responseBody,
            headers = this.infos.responseHeaders,
            byteSize = this.infos.responseByteSize,
        ),
        type = when {
            this.graphql != null -> FloconHttpRequestDomainModel.Type.GraphQl(
                query = this.graphql.query,
                operationType = this.graphql.operationType,
                isSuccess = this.graphql.isSuccess,
                httpCode = this.graphql.responseHttpCode,
            )

            this.http != null -> FloconHttpRequestDomainModel.Type.Http(
                httpCode = this.http.responseHttpCode,
            )

            this.grpc != null -> FloconHttpRequestDomainModel.Type.Grpc(
                responseStatus = this.grpc.responseStatus,
            )

            else -> return null
        },
    )
}

fun FloconHttpRequestEntityLite.toDomainModel(): FloconHttpRequestDomainModel? {
    return FloconHttpRequestDomainModel(
        uuid = this.uuid,
        url = this.url,
        durationMs = this.durationMs,
        request = FloconHttpRequestDomainModel.Request(
            method = this.method,
            startTime = this.startTime,
            headers = emptyMap(), // removed for lite
            body = null, // removed for lite
            byteSize = 0L, // removed for lite
        ),
        response = FloconHttpRequestDomainModel.Response(
            contentType = null, // removed for lite
            body = null, // removed for lite
            headers = emptyMap(), // removed for lite
            byteSize = 0L, // removed for lite
        ),
        type = when {
            this.graphql != null -> FloconHttpRequestDomainModel.Type.GraphQl(
                query = this.graphql.query,
                operationType = this.graphql.operationType,
                isSuccess = this.graphql.isSuccess,
                httpCode = this.graphql.responseHttpCode,
            )

            this.http != null -> FloconHttpRequestDomainModel.Type.Http(
                httpCode = this.http.responseHttpCode,
            )

            this.grpc != null -> FloconHttpRequestDomainModel.Type.Grpc(
                responseStatus = this.grpc.responseStatus,
            )

            else -> return null
        },
    )
}
