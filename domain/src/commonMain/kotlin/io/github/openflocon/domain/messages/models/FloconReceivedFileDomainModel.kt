package io.github.openflocon.domain.messages.models

data class FloconReceivedFileDomainModel(
    val deviceId: String,
    val remotePath: String,
    val localPath: String,
    val requestId: String,
    val appPackageName: String,
    val appInstance: Long,
)
