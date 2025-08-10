package io.github.openflocon.flocondesktop.features.files.data.datasources.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"])
    ]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val parentPath: String,
    val size: Long,
    val lastModifiedTimestamp: Long,
)
