package io.github.openflocon.domain.crashreporter.repository

import androidx.paging.PagingData
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface CrashReporterRepository {
    fun observeCrashReports(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<PagingData<CrashReportDomainModel>>
    suspend fun deleteCrashReport(crashId: String)
    suspend fun clearAll(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,)
    fun observeCrashReportById(id: String, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<CrashReportDomainModel?>
}
