package io.github.openflocon.data.local.timelinelog.datasource

import io.github.openflocon.data.local.timelinelog.models.TimelineLogEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val paramsSerializer = MapSerializer(String.serializer(), String.serializer())

internal fun TimelineLogDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
) = TimelineLogEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    appInstance = deviceIdAndPackageName.appInstance,
    timestamp = timestamp,
    type = type,
    level = level.name.uppercase(),
    value = value,
    paramsJson = Json.encodeToString(paramsSerializer, params),
)

internal fun TimelineLogEntity.toDomain() = TimelineLogDomainModel(
    id = id,
    timestamp = timestamp,
    type = type,
    level = TimelineLogLevel.entries.firstOrNull { it.name.uppercase() == level } ?: TimelineLogLevel.Info,
    value = value,
    params = try {
        Json.decodeFromString(paramsSerializer, paramsJson)
    } catch (t: Throwable) {
        emptyMap()
    },
)
