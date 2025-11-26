package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import java.io.File

interface NetworkCsvDatasource {
    suspend fun exportAsCsv(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requests: List<FloconNetworkCallDomainModel>,
        file: File
    ) : Either<Throwable, Unit>

    suspend fun importCallsFromCsv(
        file: File,
        appInstance: AppInstance
    ) : Either<Throwable, List<FloconNetworkCallDomainModel>>
}