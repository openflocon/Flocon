package com.flocon.data.remote.device.model

import kotlinx.serialization.Serializable

@Serializable
class BonjourDataModel(
    val plugins: List<String>? = null,
)
