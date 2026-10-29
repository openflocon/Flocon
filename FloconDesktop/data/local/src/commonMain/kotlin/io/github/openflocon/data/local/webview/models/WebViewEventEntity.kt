package io.github.openflocon.data.local.webview.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["deviceId", "packageName"],
            childColumns = ["deviceId", "packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class WebViewEventEntity(
    @PrimaryKey
    val eventId: String,

    val deviceId: String,
    val packageName: String,

    val webViewId: String,
    // name of WebViewEventKind
    val kind: String,
    val url: String?,
    val label: String,
    val message: String?,
    val sourceId: String?,
    val lineNumber: Int?,
    val statusCode: Int?,
    val result: String?,
    val timestamp: Long,
)
