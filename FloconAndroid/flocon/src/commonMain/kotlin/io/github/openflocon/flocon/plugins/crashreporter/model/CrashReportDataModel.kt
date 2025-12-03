package io.github.openflocon.flocon.plugins.crashreporter.model

import kotlinx.serialization.Serializable

@Serializable
data class CrashReportDataModel(
    val crashId: String,
    val timestamp: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
    val appVersion: String,
    val deviceInfo: DeviceInfoDataModel,
)

@Serializable
data class DeviceInfoDataModel(
    val deviceId: String,
    val deviceName: String,
    val osVersion: String,
    val appPackageName: String,
)
