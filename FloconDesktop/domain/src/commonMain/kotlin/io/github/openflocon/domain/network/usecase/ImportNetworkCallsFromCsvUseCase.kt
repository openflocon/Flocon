package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.common.then
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkCsvRepository
import io.github.openflocon.domain.network.repository.NetworkRepository
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class ImportNetworkCallsFromCsvUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkRepository: NetworkRepository,
    private val networkCsvRepository: NetworkCsvRepository,
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
            ?: return Failure(Throwable("no current device"))
        return try {
            val file = showOpenFileDialog(
                dialogName = "Import Sql Query",
            )
            if (file == null) {
                Failure(Throwable("no file selected"))
            } else {
                networkCsvRepository.importCallsFromCsv(file = file, current.appInstance)
                    .then {
                        networkRepository.addCalls(
                            deviceIdAndPackageName = current,
                            calls = it
                        )
                        Success(Unit)
                    }
            }
        } catch (t: Throwable) {
            Failure(t)
        }
    }

    private fun showOpenFileDialog(dialogName: String): File? {
        val parentFrame = Frame()
        val dialog = FileDialog(parentFrame, dialogName, FileDialog.LOAD)

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
