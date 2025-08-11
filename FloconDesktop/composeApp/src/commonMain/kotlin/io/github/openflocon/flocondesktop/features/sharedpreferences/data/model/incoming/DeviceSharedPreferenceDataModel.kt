package io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.incoming

import com.flocon.library.domain.models.DeviceSharedPreferenceDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class DeviceSharedPreferenceDataModel(
    val name: String,
)

fun toDeviceSharedPreferenceDomain(list: List<DeviceSharedPreferenceDataModel>): List<DeviceSharedPreferenceDomainModel> = list.map {
    DeviceSharedPreferenceDomainModel(
        id = it.name,
        name = it.name,
    )
}
