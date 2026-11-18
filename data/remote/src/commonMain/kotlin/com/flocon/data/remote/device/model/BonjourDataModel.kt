package com.flocon.data.remote.device.model

import kotlinx.serialization.Serializable

@Serializable
class BonjourDataModel(
    val plugins: List<String>? = null,
    val connectionType: String,
    // kotlin, swift, flutter... absent for an SDK older than this field
    val sdk: String? = null,
)
