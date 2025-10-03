package io.github.openflocon.data.local.network.mapper

import io.github.openflocon.data.local.network.models.FilterItemSavedEntity
import io.github.openflocon.data.local.network.models.NetworkFilterEntity
import io.github.openflocon.domain.device.models.AppPackageName
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
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
    json: Json,
    deviceId: DeviceId,
    packageName: AppPackageName,
    column: NetworkTextFilterColumns,
    domain: TextFilterStateDomainModel,
): NetworkFilterEntity {
    val itemsEntity: List<FilterItemSavedEntity> = domain.items.map {
        textFilterItemToEntity(it)
    }
    return NetworkFilterEntity(
        deviceId = deviceId,
        packageName = packageName,
        columnName = column,
        isEnabled = domain.isEnabled,
        itemsAsJson = json.encodeToString(itemsEntity),
    )
}

fun NetworkFilterEntity.textFilterToDomain(
    json: Json,
): TextFilterStateDomainModel {
    val itemsEntity = json.decodeFromString<List<FilterItemSavedEntity>>(itemsAsJson)
    return TextFilterStateDomainModel(
        isEnabled = isEnabled,
        items = itemsEntity.map {
            textFilterItemToDomain(it)
        },
    )
}
