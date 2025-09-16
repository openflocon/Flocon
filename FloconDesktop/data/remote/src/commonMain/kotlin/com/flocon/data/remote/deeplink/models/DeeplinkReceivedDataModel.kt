package com.flocon.data.remote.deeplink.models

import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkReceivedDataModel(
    val label: String? = null,
    val link: String,
    val description: String? = null,
    val parameters: List<Parameter> = emptyList(),
) {
    @Serializable
    data class Parameter(
        val paramName: String,
        val autoComplete: List<String>,
    )
}
