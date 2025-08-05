package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["deviceId", "link"], unique = true),
    ],
)
data class DeeplinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val link: String,
    val label: String?,
    val description: String?,
)
