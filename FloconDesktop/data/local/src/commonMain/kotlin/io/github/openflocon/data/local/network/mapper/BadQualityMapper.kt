package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.badquality.BadQualityConfigEntity
import io.github.openflocon.data.local.network.models.badquality.ErrorEmbedded
import io.github.openflocon.data.local.network.models.badquality.LatencyConfigEmbedded
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import kotlinx.serialization.json.Json
import kotlin.time.Instant

fun BadQualityConfigEntity.toDomain(json: Json): BadQualityConfigDomainModel {
    val errors = try {
        json.decodeFromString<List<ErrorEmbedded>>(errors)
            .map { it.toDomain() }
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
    return BadQualityConfigDomainModel(
        id = id,
        name = name,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        isEnabled = isEnabled,
        latency = BadQualityConfigDomainModel.LatencyConfig(
            triggerProbability = latency.triggerProbability,
            minLatencyMs = latency.minLatencyMs,
            maxLatencyMs = latency.maxLatencyMs,
        ),
        errorProbability = errorProbability,
        errors = errors,
    )
}

fun ErrorEmbedded.toDomain(): BadQualityConfigDomainModel.Error {
    return BadQualityConfigDomainModel.Error(
        weight = weight,
        type = type.toDomain(),
    )
}

private fun ErrorEmbedded.Type.toDomain(): BadQualityConfigDomainModel.Error.Type {
    return when (this) {
        is ErrorEmbedded.Type.Body -> BadQualityConfigDomainModel.Error.Type.Body(
            httpCode = httpCode,
            body = body,
            contentType = contentType,
        )

        is ErrorEmbedded.Type.Exception -> BadQualityConfigDomainModel.Error.Type.Exception(
            classPath = classPath,
        )
    }
}


fun BadQualityConfigDomainModel.toEntity(
    json: Json,
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
): BadQualityConfigEntity {
    val errorsEmbedded = errors.map {
        it.toEntity()
    }
    val errors = try {
        json.encodeToString<List<ErrorEmbedded>>(errorsEmbedded)
    } catch (t: Throwable) {
        t.printStackTrace()
        "[]"
    }
    return BadQualityConfigEntity(
        id = id,
        name = name,
        createdAt = createdAt.toEpochMilliseconds(),
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        isEnabled = isEnabled,
        latency = LatencyConfigEmbedded(
            triggerProbability = latency.triggerProbability,
            minLatencyMs = latency.minLatencyMs,
            maxLatencyMs = latency.maxLatencyMs,
        ),
        errorProbability = errorProbability,
        errors = errors,
    )
}

private fun BadQualityConfigDomainModel.Error.toEntity(): ErrorEmbedded = ErrorEmbedded(
    weight = weight,
    type = this.type.toEntity(),
)

private fun BadQualityConfigDomainModel.Error.Type.toEntity() = when (this) {
    is BadQualityConfigDomainModel.Error.Type.Body -> ErrorEmbedded.Type.Body(
        httpCode = httpCode,
        body = body,
        contentType = contentType,
    )

    is BadQualityConfigDomainModel.Error.Type.Exception -> ErrorEmbedded.Type.Exception(
        classPath = classPath,
    )
}
