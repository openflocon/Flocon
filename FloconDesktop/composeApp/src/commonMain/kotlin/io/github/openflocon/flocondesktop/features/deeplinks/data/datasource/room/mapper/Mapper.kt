package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.mapper

import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity
import io.github.openflocon.domain.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

fun DeeplinkEntity.toDomainModel(): DeeplinkDomainModel = DeeplinkDomainModel(
    label = this.label,
    link = this.link,
    description = this.description,
)

fun DeeplinkDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
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
    )
}

// Pour une liste
fun toDomainModels(entities: List<DeeplinkEntity>): List<DeeplinkDomainModel> = entities.map { it.toDomainModel() }

fun toEntities(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>): List<DeeplinkEntity> = deeplinks.map {
    it.toEntity(deviceIdAndPackageName = deviceIdAndPackageName)
}
