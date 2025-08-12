package com.flocon.data.remote.deeplink.models

import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkReceivedDataModel(
    val label: String? = null,
    val link: String,
    val description: String? = null,
)
