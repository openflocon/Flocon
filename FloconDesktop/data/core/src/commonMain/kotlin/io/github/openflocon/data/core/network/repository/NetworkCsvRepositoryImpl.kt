package io.github.openflocon.data.core.network.repository

import io.github.openflocon.data.core.network.datasource.NetworkCsvDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.repository.NetworkCsvRepository
import kotlinx.coroutines.withContext
import java.io.File

class NetworkCsvRepositoryImpl(
    private val csvDatasource: NetworkCsvDatasource,
    private val dispatcherProvider: DispatcherProvider,
) : NetworkCsvRepository {

    override suspend fun exportAsCsv(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requests: List<FloconNetworkCallDomainModel>,
        file: File
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        csvDatasource.exportAsCsv(
            deviceIdAndPackageName = deviceIdAndPackageName,
            requests = requests,
            file = file,
        )
    }

    override suspend fun importCallsFromCsv(
        file: File,
        appInstance: AppInstance
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> = withContext(dispatcherProvider.data) {
        csvDatasource.importCallsFromCsv(
            file = file,
            appInstance = appInstance,
        )
    }
}
