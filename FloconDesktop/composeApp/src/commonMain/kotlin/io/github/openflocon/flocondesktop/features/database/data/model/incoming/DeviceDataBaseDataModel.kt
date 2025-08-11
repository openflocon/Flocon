package io.github.openflocon.flocondesktop.features.database.data.model.incoming

import com.flocon.library.domain.models.DeviceDataBaseDomainModel
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
