package io.github.openflocon.flocondesktop.features.graphql.data.datasource.room.mapper

import com.florent37.flocondesktop.features.graphql.data.datasource.room.model.GraphQlRequestEntity
import com.florent37.flocondesktop.features.graphql.domain.model.FloconGraphQlRequestInfos
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel

fun GraphQlRequestDomainModel.toEntity(deviceId: String): GraphQlRequestEntity = GraphQlRequestEntity(
    uuid = this.uuid,
    request = with(this.infos.request) {
        GraphQlRequestEntity.Request(
            method = this.method,
            startTime = this.startTime,
            headers = this.headers,
            body = this.body,
            byteSize = this.byteSize,
        )
    },
    response = with(this.infos.response) {
        GraphQlRequestEntity.Response(
            httpCode = this.httpCode,
            contentType = this.contentType,
            body = this.body,
            headers = this.headers,
            byteSize = this.byteSize,
        )
    },
    deviceId = deviceId,
    url = this.infos.url,
    durationMs = this.infos.durationMs,
)

fun GraphQlRequestEntity.toDomainModel(): GraphQlRequestDomainModel = GraphQlRequestDomainModel(
    uuid = this.uuid,
    infos = FloconGraphQlRequestInfos(
        url = this.url,
        durationMs = this.durationMs,
        request = with(this.request) {
            FloconGraphQlRequestInfos.Request(
                method = this.method,
                startTime = this.startTime,
                headers = headers,
                body = body,
                byteSize = byteSize,
            )
        },
        response = with(this.response) {
            FloconGraphQlRequestInfos.Response(
                httpCode = httpCode,
                contentType = contentType,
                body = body,
                headers = headers,
                byteSize = byteSize,
            )
        },
    ),
)
