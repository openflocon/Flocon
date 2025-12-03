package io.github.openflocon.data.local.crashreporter.mapper

import io.github.openflocon.data.local.crashreporter.models.CrashReportEntity
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

internal fun CrashReportDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
): CrashReportEntity {
    return CrashReportEntity(
        crashId = id,
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        timestamp = timestamp,
        exceptionType = exceptionType,
        exceptionMessage = exceptionMessage,
        stackTrace = stackTrace,
    )
}

internal fun CrashReportEntity.toDomain(): CrashReportDomainModel {
    return CrashReportDomainModel(
        id = crashId,
        timestamp = timestamp,
        exceptionType = exceptionType,
        exceptionMessage = exceptionMessage,
        stackTrace = stackTrace,
    )
}