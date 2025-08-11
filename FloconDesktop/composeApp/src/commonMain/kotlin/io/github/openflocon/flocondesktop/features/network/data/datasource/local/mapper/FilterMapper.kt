package io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper

import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FilterItemSavedEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.NetworkFilterEntity
import io.github.openflocon.domain.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import kotlinx.serialization.json.Json

fun textFilterItemToEntity(item: TextFilterStateDomainModel.FilterItem): FilterItemSavedEntity = FilterItemSavedEntity(
    text = item.text,
    isActive = item.isActive,
    isExcluded = item.isExcluded,
)

fun textFilterItemToDomain(item: FilterItemSavedEntity): TextFilterStateDomainModel.FilterItem = TextFilterStateDomainModel.FilterItem(
    text = item.text,
    isActive = item.isActive,
    isExcluded = item.isExcluded,
)

fun textFilterToEntity(
    deviceId: DeviceId,
    column: NetworkTextFilterColumns,
    domain: TextFilterStateDomainModel,
): NetworkFilterEntity {
    val itemsEntity: List<FilterItemSavedEntity> = domain.items.map {
        textFilterItemToEntity(it)
    }
    return NetworkFilterEntity(
        deviceId = deviceId,
        columnName = column,
        isEnabled = domain.isEnabled,
        itemsAsJson = Json.encodeToString(itemsEntity),
    )
}

fun textFilterToDomain(
    entity: NetworkFilterEntity,
): TextFilterStateDomainModel {
    val itemsEntity = Json.decodeFromString<List<FilterItemSavedEntity>>(entity.itemsAsJson)
    return TextFilterStateDomainModel(
        isEnabled = entity.isEnabled,
        items = itemsEntity.map {
            textFilterItemToDomain(it)
        },
    )
}
