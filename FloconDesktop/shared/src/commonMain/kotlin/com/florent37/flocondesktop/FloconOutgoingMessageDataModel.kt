package com.florent37.flocondesktop

import kotlinx.serialization.Serializable

@Serializable
data class FloconOutgoingMessageDataModel(
    val plugin: String,
    val method: String,
    val body: String,
)
