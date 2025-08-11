package com.flocon.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class FloconIncomingMessageDataModel(
    val deviceName: String,
    val deviceId: String,
    val plugin: String,
    val body: String,
    val method: String,
    val appName: String,
    val appPackageName: String,
)
