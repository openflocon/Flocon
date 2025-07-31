package io.github.openflocon.flocondesktop.features.database.data.model.incoming

import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
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
