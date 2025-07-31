package com.florent37.flocondesktop.features.graphql.data.datasource.room.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["uuid"], unique = true),
    ],
)
data class GraphQlRequestEntity(
    @PrimaryKey val uuid: String,
    val deviceId: String,
    val url: String,
    val durationMs: Double,
    @Embedded(prefix = "request_") val request: Request,
    @Embedded(prefix = "response_") val response: Response,
) {
    data class Request(
        val startTime: Long,
        val method: String,
        val body: String?,
        val byteSize: Long,
        val headers: Map<String, String>,
    )

    data class Response(
        val httpCode: Int,
        val contentType: String?,
        val body: String?,
        val byteSize: Long,
        val headers: Map<String, String>,
    )
}
