package io.github.openflocon.data.local.crashreporter.datasource

import io.github.openflocon.data.core.crashreporter.datasources.CrashReporterLocalDataSource
import io.github.openflocon.data.local.crashreporter.dao.CrashReportDao
import io.github.openflocon.data.local.crashreporter.mapper.toDomain
import io.github.openflocon.data.local.crashreporter.mapper.toEntity
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CrashReporterLocalDataSourceRoom(
    private val crashReportDao: CrashReportDao,
) : CrashReporterLocalDataSource {

    override suspend fun insertAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        crashes: List<CrashReportDomainModel>
    ) {
        crashReportDao.insertAll(crashes.map { it.toEntity(
            deviceIdAndPackageName = deviceIdAndPackageName
        ) })
    }

    override fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<List<CrashReportDomainModel>> {
        return crashReportDao.observeAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun delete(crashId: String) {
        crashReportDao.delete(crashId)
    }

    override suspend fun clearAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        crashReportDao.clearAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )
    }

    override fun observeCrashReportById(
        id: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<CrashReportDomainModel?> {
        return crashReportDao.observeCrashReportById(
            id = id,
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        ).map { it?.toDomain() }
    }

}
