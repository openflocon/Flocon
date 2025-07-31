package com.florent37.flocondesktop.features.network.data.datasource.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FloconHttpRequestEntity(
    @PrimaryKey
    val uuid: String,
    val deviceId: String, // To associate with a device
    @Embedded
    val infos: FloconHttpRequestInfosEntity,
)

data class FloconHttpRequestInfosEntity(
    val url: String,
    val method: String,
    val startTime: Long,
    val durationMs: Double,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val requestByteSize: Long,
    val responseHttpCode: Int,
    val responseContentType: String?,
    val responseBody: String?,
    val responseHeaders: Map<String, String>,
    val responseByteSize: Long,
)
