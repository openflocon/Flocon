package com.flocon.data.remote.sharedpreference.models

import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
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
