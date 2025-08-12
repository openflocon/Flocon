package io.github.openflocon.domain.messages.models

data class FloconIncomingMessageDomainModel(
    val deviceName: String,
    val deviceId: String,
    val plugin: String,
    val body: String,
    val method: String,
    val appName: String,
    val appPackageName: String,
)
