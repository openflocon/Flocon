package io.github.openflocon.data.core.crashreporter.repository

import co.touchlab.kermit.Logger
import io.github.openflocon.data.core.crashreporter.datasources.CrashReportRemoteDataSource
import io.github.openflocon.data.core.crashreporter.datasources.CrashReporterLocalDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CrashReporterRepositoryImpl(
    private val localDataSource: CrashReporterLocalDataSource,
    private val remoteDataSource: CrashReportRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : CrashReporterRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.CrashReporter.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        if (message.method == Protocol.FromDevice.CrashReporter.Method.ReportCrash) {
            try {
                val crashes = remoteDataSource.getItems(message)
                addCrashReports(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    crashes = crashes
                )
            } catch (t: Throwable) {
                Logger.e("error on message received / crashReporter", t)
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean
    ) {
        // No-op
    }

    private suspend fun addCrashReports(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        crashes: List<CrashReportDomainModel>
    ) {
        withContext(dispatcherProvider.domain) {
            localDataSource.insertAll(
                deviceIdAndPackageName = deviceIdAndPackageName,
                crashes = crashes
            )
        }
    }

    override fun observeCrashReports(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<CrashReportDomainModel>> = localDataSource.observeAll(
        deviceIdAndPackageName = deviceIdAndPackageName,
    ).flowOn(dispatcherProvider.data)

    override suspend fun deleteCrashReport(crashId: String) {
        withContext(dispatcherProvider.data) {
            localDataSource.delete(crashId)
        }
    }

    override suspend fun clearAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            localDataSource.clearAll(deviceIdAndPackageName)
        }
    }

    override fun observeCrashReportById(
        id: String,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<CrashReportDomainModel?> = localDataSource.observeCrashReportById(
        id = id,
        deviceIdAndPackageName = deviceIdAndPackageName,
    )
        .flowOn(dispatcherProvider.data)
}
