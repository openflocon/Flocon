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
import io.github.openflocon.domain.network.models.httpCode
import io.github.openflocon.domain.network.models.isImage
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class NetworkCsvDatasourceImpl() : NetworkCsvDatasource {
    override suspend fun exportAsCsv(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requests: List<FloconNetworkCallDomainModel>,
        file: File
    ): Either<Throwable, Unit> {
        return try {
            requests.exportToCsv(file)
            Unit.success()
        } catch (t: Throwable) {
            t.failure()
        }
    }

    override suspend fun importCallsFromCsv(
        file: File,
        appInstance: AppInstance
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> {
        return try {
            importNetworkCallsFromCsv(
                file = file,
                appInstance = appInstance
            ).success()
        } catch (t: Throwable) {
            t.failure()
        }
    }
}
