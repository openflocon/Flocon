package io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper

import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestEntity
import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestInfosEntity
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel

fun FloconHttpRequestDomainModel.toEntity(deviceId: String): FloconHttpRequestEntity = FloconHttpRequestEntity(
    uuid = this.uuid,
    infos = this.toInfosEntity(),
    deviceId = deviceId,
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
)
