package com.flocon.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class FloconOutgoingMessageDataModel(
    val plugin: String,
    val method: String,
    val body: String,
)
