package com.flocon.data.remote.database.models

import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDataBaseDataModel(
    val id: String,
    val name: String,
)

fun toDeviceDatabasesDomain(list: List<DeviceDataBaseDataModel>): List<DeviceDataBaseDomainModel> = list.map {
    DeviceDataBaseDomainModel(
        id = it.id,
        name = it.name,
    )
}
