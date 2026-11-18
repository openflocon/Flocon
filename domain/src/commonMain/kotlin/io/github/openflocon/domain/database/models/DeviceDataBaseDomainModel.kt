package io.github.openflocon.domain.database.models

typealias DeviceDataBaseId = String

data class DeviceDataBaseDomainModel(
    val id: DeviceDataBaseId,
    val name: String,
)
