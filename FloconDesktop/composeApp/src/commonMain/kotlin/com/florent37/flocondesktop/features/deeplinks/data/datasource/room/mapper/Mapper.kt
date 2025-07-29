package com.florent37.flocondesktop.features.deeplinks.data.datasource.room.mapper

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity
import com.florent37.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel

fun DeeplinkEntity.toDomainModel(): DeeplinkDomainModel = DeeplinkDomainModel(
    label = this.label,
    link = this.link,
    description = this.description,
)

fun DeeplinkDomainModel.toEntity(
    deviceId: String,
): DeeplinkEntity {
    // Note: L'ID sera généré automatiquement par Room lors de l'insertion,
    // donc nous n'avons pas besoin de le spécifier ici si nous faisons une nouvelle insertion.
    // Si vous mettez à jour une entité existante, vous devrez peut-être passer l'ID.
    return DeeplinkEntity(
        link = this.link,
        deviceId = deviceId,
        label = this.label,
        description = this.description,
    )
}

// Pour une liste
fun toDomainModels(entities: List<DeeplinkEntity>): List<DeeplinkDomainModel> = entities.map { it.toDomainModel() }

fun toEntities(deeplinks: List<DeeplinkDomainModel>, deviceId: DeviceId): List<DeeplinkEntity> = deeplinks.map { it.toEntity(deviceId = deviceId) }
