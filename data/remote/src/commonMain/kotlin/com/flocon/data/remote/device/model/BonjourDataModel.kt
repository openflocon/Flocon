package com.flocon.data.remote.device.model

import kotlinx.serialization.Serializable

@Serializable
class BonjourDataModel(
    val plugins: List<String>? = null,
    // absent when the proxy sends calls for a device of the SDK: the connection type of the SDK is kept
    val connectionType: String? = null,
    // kotlin, swift, flutter... absent for an SDK older than this field
    val sdk: String? = null,
)
