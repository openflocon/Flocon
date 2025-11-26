package io.github.openflocon.data.core.network.repository

import io.github.openflocon.data.core.network.datasource.NetworkCsvDatasource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.models.isImage
import io.github.openflocon.domain.network.repository.NetworkCsvRepository
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class NetworkCsvRepositoryImpl(
    private val csvDatasource: NetworkCsvDatasource,
    private val dispatcherProvider: DispatcherProvider,
) : NetworkCsvRepository {

    override suspend fun exportAsCsv(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requests: List<FloconNetworkCallDomainModel>,
        file: File
    ): Either<Throwable, Unit> {
        return withContext(dispatcherProvider.data) {
            csvDatasource.exportAsCsv(
                deviceIdAndPackageName = deviceIdAndPackageName,
                requests = requests,
                file = file,
            )
        }
    }

    override suspend fun importCallsFromCsv(
        file: File,
        appInstance: AppInstance
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> {
        return withContext(dispatcherProvider.data) {
            csvDatasource.importCallsFromCsv(
                file = file,
                appInstance = appInstance,
            )
        }
    }

}