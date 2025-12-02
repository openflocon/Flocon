package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkCsvDatasource
import io.github.openflocon.data.local.network.utils.exportToCsv
import io.github.openflocon.data.local.network.utils.importNetworkCallsFromCsv
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.device.models.AppInstance
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import java.io.File

class NetworkCsvDatasourceImpl : NetworkCsvDatasource {
    override suspend fun exportAsCsv(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requests: List<FloconNetworkCallDomainModel>,
        file: File
    ): Either<Throwable, Unit> = try {
        requests.exportToCsv(file)
        Unit.success()
    } catch (t: Throwable) {
        t.failure()
    }

    override suspend fun importCallsFromCsv(
        file: File,
        appInstance: AppInstance
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> = try {
        importNetworkCallsFromCsv(
            file = file,
            appInstance = appInstance
        ).success()
    } catch (t: Throwable) {
        t.failure()
    }
}
