package io.github.openflocon.data.local.crashreporter.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.github.openflocon.data.core.crashreporter.datasources.CrashReporterLocalDataSource
import io.github.openflocon.data.local.crashreporter.dao.CrashReportDao
import io.github.openflocon.data.local.crashreporter.mapper.toDomain
import io.github.openflocon.data.local.crashreporter.mapper.toEntity
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CrashReporterLocalDataSourceRoom(
    private val crashReportDao: CrashReportDao,
) : CrashReporterLocalDataSource {

    override suspend fun insertAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        crashes: List<CrashReportDomainModel>
    ) {
        crashReportDao.insertAll(
            crashes.map {
                it.toEntity(
                    deviceIdAndPackageName = deviceIdAndPackageName
                )
            }
        )
    }

    override fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<PagingData<CrashReportDomainModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            crashReportDao.observeAll(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
        }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
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
    ): Flow<CrashReportDomainModel?> = crashReportDao.observeCrashReportById(
        id = id,
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    ).map { it?.toDomain() }
}
