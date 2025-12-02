package io.github.openflocon.data.local.deeplink.mapper

import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.deeplink.models.DeeplinkParameterEntity
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun DeeplinkEntity.toDomainModel(
    json: Json,
): DeeplinkDomainModel = DeeplinkDomainModel(
    label = this.label,
    link = this.link,
    description = this.description,
    id = this.id,
    parameters = try {
        this.parametersAsJson?.let {
            json.decodeFromString<List<DeeplinkParameterEntity>>(it)
        }?.map { it.toDomain() } ?: emptyList()
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    },
)

fun DeeplinkDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    isHistory: Boolean,
    json: Json,
): DeeplinkEntity {
    // Note: L'ID sera généré automatiquement par Room lors de l'insertion,
    // donc nous n'avons pas besoin de le spécifier ici si nous faisons une nouvelle insertion.
    // Si vous mettez à jour une entité existante, vous devrez peut-être passer l'ID.
    return DeeplinkEntity(
        link = link,
        deviceId = deviceIdAndPackageName.deviceId,
        label = label,
        packageName = deviceIdAndPackageName.packageName,
        description = description,
        isHistory = isHistory,
        parametersAsJson = try {
            json.encodeToString(parameters.map { it.toEntity() })
        } catch (t: Throwable) {
            t.printStackTrace()
            "[]"
        },
    )
}

// Pour une liste
fun toDomainModels(
    entities: List<DeeplinkEntity>,
    json: Json,
): List<DeeplinkDomainModel> = entities.map { it.toDomainModel(json = json) }

fun toEntities(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    deeplinks: List<DeeplinkDomainModel>,
    json: Json,
): List<DeeplinkEntity> = deeplinks.map {
    it.toEntity(deviceIdAndPackageName = deviceIdAndPackageName, isHistory = false, json = json,)
}

private fun DeeplinkDomainModel.Parameter.toEntity() = DeeplinkParameterEntity(
    paramName = paramName,
    autoComplete = autoComplete,
)

private fun DeeplinkParameterEntity.toDomain() = DeeplinkDomainModel.Parameter(
    paramName = paramName,
    autoComplete = autoComplete,
)
