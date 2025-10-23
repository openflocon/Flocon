package io.github.openflocon.domain.device.models

data class DeviceAppDomainModel(
    val name: String,
    val packageName: AppPackageName,
    val iconEncoded: String?,
    val lastAppInstance: AppInstance, // last start app time
    val floconVersionOnDevice: String,
)
