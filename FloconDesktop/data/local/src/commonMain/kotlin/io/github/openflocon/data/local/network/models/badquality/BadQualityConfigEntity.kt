package io.github.openflocon.data.local.network.models.badquality

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
    ],
)
data class BadQualityConfigEntity(
    @PrimaryKey val id: String,
    val name: String,
    val deviceId: String,
    val createdAt: Long,
    val packageName: String,
    val isEnabled: Boolean,
    @Embedded val latency: LatencyConfigEmbedded,
    val errorProbability: Double,
    val errors: String, // saved as json
)

data class LatencyConfigEmbedded(
    val triggerProbability: Double,
    val minLatencyMs: Long,
    val maxLatencyMs: Long,
)
