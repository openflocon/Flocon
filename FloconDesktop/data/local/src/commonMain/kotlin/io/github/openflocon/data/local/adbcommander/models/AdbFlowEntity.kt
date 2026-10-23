package io.github.openflocon.data.local.adbcommander.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId"]),
    ],
)
data class AdbFlowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val name: String,
    val description: String?,
    val createdAt: Long,
)
