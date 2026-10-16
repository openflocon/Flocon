package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkCsvRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import java.io.File

class ExportNetworkCallsToCsvUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkRepository: NetworkRepository,
    private val networkCsvRepository: NetworkCsvRepository,
) {

    suspend operator fun invoke(
        ids: List<String>
    ): Either<Throwable, String> {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return Failure(Throwable("No device id"))

        val baseName = "network_calls_${System.currentTimeMillis()}"
        val selectedFile = FileKit.openFileSaver(
            suggestedName = baseName,
            defaultExtension = "csv",
        ) ?: return Failure(Throwable("no file selected"))

        val file = File(selectedFile.path)
        val requests = networkRepository.getRequests(
            deviceIdAndPackageName = deviceIdAndPackageName,
            ids = ids
        )

        networkCsvRepository.exportAsCsv(
            deviceIdAndPackageName = deviceIdAndPackageName,
            requests = requests,
            file = file
        )

        return Success(file.absolutePath)
    }
}
