package io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper

import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestInfosEntity
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel

fun FloconHttpRequestDomainModel.toEntity(deviceId: String): FloconHttpRequestEntity = FloconHttpRequestEntity(
    uuid = this.uuid,
    infos = this.toInfosEntity(),
    deviceId = deviceId,
    graphql = when (val t = this.type) {
        is FloconHttpRequestDomainModel.Type.GraphQl -> FloconHttpRequestEntity.FloconHttpRequestGraphQlEntity(
            query = t.query,
            operationType = t.operationType,
            isSuccess = t.isSuccess,
        )
        else -> null
    },
)

private fun FloconHttpRequestDomainModel.toInfosEntity(): FloconHttpRequestInfosEntity = FloconHttpRequestInfosEntity(
    url = this.url,
    method = this.request.method,
    startTime = this.request.startTime,
    durationMs = this.durationMs,
    requestHeaders = this.request.headers,
    requestBody = this.request.body,
    requestByteSize = this.request.byteSize,
    responseHttpCode = this.response.httpCode,
    responseContentType = this.response.contentType,
    responseBody = this.response.body,
    responseHeaders = this.response.headers,
    responseByteSize = this.response.byteSize,
)

fun FloconHttpRequestEntity.toDomainModel(): FloconHttpRequestDomainModel = FloconHttpRequestDomainModel(
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
        httpCode = this.infos.responseHttpCode,
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
        )
        else -> FloconHttpRequestDomainModel.Type.Http
    },
)
