package io.github.openflocon.domain.adb.model

sealed interface AdbCommandTargetDomainModel {
    data class Device(val deviceId: String) : AdbCommandTargetDomainModel
    data object AllDevices : AdbCommandTargetDomainModel
}
