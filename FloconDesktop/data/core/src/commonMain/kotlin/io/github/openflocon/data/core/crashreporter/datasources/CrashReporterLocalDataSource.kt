package io.github.openflocon.data.core.crashreporter.datasources

import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface CrashReporterLocalDataSource {
    suspend fun insertAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        crashes: List<CrashReportDomainModel>
    )
    fun observeAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<CrashReportDomainModel>>
    suspend fun delete(crashId: String)
    suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
    fun observeCrashReportById(id: String): Flow<CrashReportDomainModel?>
}