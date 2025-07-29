package com.florent37.flocondesktop.features.network.data.datasource.local.mapper

import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestEntity
import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestInfosEntity
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestInfos

fun FloconHttpRequestDomainModel.toEntity(deviceId: String): FloconHttpRequestEntity = FloconHttpRequestEntity(
    uuid = this.uuid,
    infos = this.infos.toInfosEntity(),
    deviceId = deviceId,
)

fun FloconHttpRequestInfos.toInfosEntity(): FloconHttpRequestInfosEntity = FloconHttpRequestInfosEntity(
    url = this.url,
    method = this.method,
    startTime = this.startTime,
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
    infos = this.infos.toInfosDomainModel(),
)

fun FloconHttpRequestInfosEntity.toInfosDomainModel(): FloconHttpRequestInfos = FloconHttpRequestInfos(
    url = this.url,
    method = this.method,
    startTime = this.startTime,
    durationMs = this.durationMs,
    request = FloconHttpRequestInfos.Request(
        headers = this.requestHeaders,
        body = this.requestBody,
        byteSize = this.requestByteSize,
    ),
    response = FloconHttpRequestInfos.Response(
        httpCode = this.responseHttpCode,
        contentType = this.responseContentType,
        body = this.responseBody,
        headers = this.responseHeaders,
        byteSize = this.responseByteSize,
    ),
)
