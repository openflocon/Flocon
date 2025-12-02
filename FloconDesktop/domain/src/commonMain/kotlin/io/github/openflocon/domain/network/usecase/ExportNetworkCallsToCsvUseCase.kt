package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkCsvRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import java.awt.FileDialog
import java.awt.Frame
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

        val fileName = "network_calls_${System.currentTimeMillis()}.csv"
        val file = showSaveFileDialog(defaultFileName = fileName, dialogName = "Export network calls as CSV") ?: return Failure(
            Throwable("no file selected")
        )

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

    private fun showSaveFileDialog(dialogName: String, defaultFileName: String): File? {
        val parentFrame = Frame()
        val dialog = FileDialog(parentFrame, dialogName, FileDialog.SAVE).apply {
            file = defaultFileName
        }

        dialog.isVisible = true // Bloque jusqu'à ce que la boîte de dialogue soit fermée

        val file = dialog.file
        val directory = dialog.directory

        parentFrame.dispose()

        return if (file != null && directory != null) {
            File(directory, file)
        } else {
            null
        }
    }
}
