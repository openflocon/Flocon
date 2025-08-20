package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.badquality.BadQualityConfigEntity
import io.github.openflocon.data.local.network.models.badquality.ErrorEmbedded
import io.github.openflocon.data.local.network.models.badquality.LatencyConfigEmbedded
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import kotlinx.serialization.json.Json
import kotlin.time.Instant

fun toDomain(json: Json, entity: BadQualityConfigEntity): BadQualityConfigDomainModel {
    val errors = try {
        json.decodeFromString<List<ErrorEmbedded>>(entity.errors)
            .map { toDomain(it) }
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
    return BadQualityConfigDomainModel(
        id = entity.id,
        name = entity.name,
        createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
        isEnabled = entity.isEnabled,
        latency = BadQualityConfigDomainModel.LatencyConfig(
            triggerProbability = entity.latency.triggerProbability,
            minLatencyMs = entity.latency.minLatencyMs,
            maxLatencyMs = entity.latency.maxLatencyMs,
        ),
        errorProbability = entity.errorProbability,
        errors = errors,
    )
}

fun toDomain(error: ErrorEmbedded): BadQualityConfigDomainModel.Error {
    return BadQualityConfigDomainModel.Error(
        weight = error.weight,
        httpCode = error.httpCode,
        body = error.body,
        contentType = error.contentType,
    )
}


fun toEntity(
    json: Json,
    config: BadQualityConfigDomainModel,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
): BadQualityConfigEntity {
    val errorsEmbedded = config.errors.map {
        toEntity(it)
    }
    val errors = try {
        json.encodeToString<List<ErrorEmbedded>>(errorsEmbedded)
    } catch (t: Throwable) {
        t.printStackTrace()
        "[]"
    }
    return BadQualityConfigEntity(
        id = config.id,
        name = config.name,
        createdAt = config.createdAt.toEpochMilliseconds(),
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        isEnabled = config.isEnabled,
        latency = LatencyConfigEmbedded(
            triggerProbability = config.latency.triggerProbability,
            minLatencyMs = config.latency.minLatencyMs,
            maxLatencyMs = config.latency.maxLatencyMs,
        ),
        errorProbability = config.errorProbability,
        errors = errors,
    )
}

private fun toEntity(error: BadQualityConfigDomainModel.Error): ErrorEmbedded = ErrorEmbedded(
    weight = error.weight,
    httpCode = error.httpCode,
    body = error.body,
    contentType = error.contentType,
)
