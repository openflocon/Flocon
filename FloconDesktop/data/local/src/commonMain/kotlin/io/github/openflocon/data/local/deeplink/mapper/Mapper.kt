package io.github.openflocon.data.local.deeplink.mapper

import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.deeplink.models.DeeplinkVariableEntity
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.json.Json

fun DeeplinkEntity.toDomainModel(
    json: Json
): DeeplinkDomainModel = DeeplinkDomainModel(
    label = this.label,
    link = this.link,
    description = this.description,
    id = this.id,
    parameters = try {
        json.decodeFromString<List<DeeplinkEntity.Parameter>>(parametersAsJson)
            .map(DeeplinkEntity.Parameter::toDomain)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
)

fun DeeplinkVariableEntity.toDomainModel(): DeeplinkVariableDomainModel = DeeplinkVariableDomainModel(
    name = name,
    mode = when (mode) {
        is DeeplinkVariableEntity.Mode.AutoComplete -> DeeplinkVariableDomainModel.Mode.AutoComplete(mode.suggestions)
        DeeplinkVariableEntity.Mode.Input -> DeeplinkVariableDomainModel.Mode.Input
    },
    description = description
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

fun DeeplinkVariableDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    isHistory: Boolean
): DeeplinkVariableEntity = DeeplinkVariableEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    name = name,
    packageName = deviceIdAndPackageName.packageName,
    description = description,
    isHistory = isHistory,
    mode = when (val mode = mode) {
        is DeeplinkVariableDomainModel.Mode.AutoComplete -> DeeplinkVariableEntity.Mode.AutoComplete(mode.suggestions)
        DeeplinkVariableDomainModel.Mode.Input -> DeeplinkVariableEntity.Mode.Input
    }
)

fun toDomainModels(
    entities: List<DeeplinkEntity>,
    json: Json,
): List<DeeplinkDomainModel> = entities.map { it.toDomainModel(json = json) }

fun toDomainModels(
    entities: List<DeeplinkVariableEntity>
): List<DeeplinkVariableDomainModel> = entities.map(DeeplinkVariableEntity::toDomainModel)

fun toEntities(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    deeplinks: List<DeeplinkDomainModel>,
    json: Json,
): List<DeeplinkEntity> = deeplinks.map {
    it.toEntity(deviceIdAndPackageName = deviceIdAndPackageName, isHistory = false, json = json)
}

fun toEntities(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    variables: List<DeeplinkVariableDomainModel>,
): List<DeeplinkVariableEntity> = variables.map {
    it.toEntity(
        deviceIdAndPackageName = deviceIdAndPackageName,
        isHistory = false
    )
}

private fun DeeplinkDomainModel.Parameter.toEntity() = when (this) {
    is DeeplinkDomainModel.Parameter.AutoComplete -> DeeplinkEntity.Parameter.AutoComplete(
        name = name,
        autoComplete = autoComplete
    )

    is DeeplinkDomainModel.Parameter.Variable -> DeeplinkEntity.Parameter.Variable(
        name = name,
        variableName = variableName
    )
}

private fun DeeplinkEntity.Parameter.toDomain() = when (this) {
    is DeeplinkEntity.Parameter.AutoComplete -> DeeplinkDomainModel.Parameter.AutoComplete(
        name = name,
        autoComplete = autoComplete
    )

    is DeeplinkEntity.Parameter.Variable -> DeeplinkDomainModel.Parameter.Variable(
        name = name,
        variableName = variableName
    )
}
