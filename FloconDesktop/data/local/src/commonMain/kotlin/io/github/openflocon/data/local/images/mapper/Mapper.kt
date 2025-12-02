package io.github.openflocon.data.local.images.mapper

import io.github.openflocon.data.local.images.models.DeviceImageEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.serialization.json.Json

fun DeviceImageEntity.toDomainModel(json: Json): DeviceImageDomainModel = DeviceImageDomainModel(
    url = this.url,
    time = this.time,
    headers = this.headersJsonEncoded.decodeMap(json),
)

private fun String.decodeMap(json: Json): Map<String, String> = try {
    json.decodeFromString<Map<String, String>>(this)
} catch (t: Throwable) {
    t.printStackTrace()
    emptyMap()
}

private fun Map<String, String>.encodeMap(json: Json): String = try {
    json.encodeToString<Map<String, String>>(this)
} catch (t: Throwable) {
    t.printStackTrace()
    "[]"
}

fun DeviceImageDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    json: Json,
): DeviceImageEntity = DeviceImageEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    url = this.url,
    time = this.time,
    headersJsonEncoded = this.headers.encodeMap(json),
)
