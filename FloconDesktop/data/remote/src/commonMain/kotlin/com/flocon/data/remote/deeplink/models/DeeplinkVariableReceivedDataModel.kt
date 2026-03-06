package com.flocon.data.remote.deeplink.models

import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkVariableReceivedDataModel(
    val name: String,
    val description: String? = null
)
