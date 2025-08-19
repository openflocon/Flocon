package io.github.openflocon.data.local.network.models.badquality

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["deviceId", "packageName"])
data class BadQualityConfigEntity(
    val deviceId: String,
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
