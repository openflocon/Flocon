package io.github.openflocon.domain.device.models

data class DeviceAppDomainModel(
    val name: String,
    val packageName: AppPackageName,
    val iconEncoded: String?,
    val lastAppInstance: AppInstance, // last start app time
    val floconVersionOnDevice: String,
    // plugins installed in the SDK of the app, null when unknown (older SDK that does not send them)
    val installedPlugins: Set<String>? = null,
)
