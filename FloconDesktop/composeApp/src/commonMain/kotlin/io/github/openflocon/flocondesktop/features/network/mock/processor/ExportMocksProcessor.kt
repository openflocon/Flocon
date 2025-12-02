package io.github.openflocon.flocondesktop.features.network.mock.processor

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlin.time.Clock

sealed interface ExportResult {
    object Success : ExportResult
    object Cancelled : ExportResult
    data class Failure(val error: Throwable) : ExportResult
}

class ExportMocksProcessor(
    private val observeNetworkMocksUseCase: ObserveNetworkMocksUseCase,
) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend operator fun invoke(): ExportResult {
        val mocks = observeNetworkMocksUseCase().firstOrNull() ?: return ExportResult.Failure(Throwable("no mocks to export"))

        val jsonString = try {
            val serialized = mocks.map { it.toExportedModel() }
            json.encodeToString(serialized)
        } catch (e: Exception) {
            Logger.e("Error exporting mocks", e)
            return ExportResult.Failure(e)
        }

        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedDate = with(localDateTime) {
            val day = dayOfMonth.toString().padStart(2, '0')
            val month = monthNumber.toString().padStart(2, '0')
            val year = year.toString()
            val hour = hour.toString().padStart(2, '0')
            val minute = minute.toString().padStart(2, '0')

            "${day}_${month}_${year}_${hour}_$minute"
        }
        val selectedFile = showSaveFileDialog(defaultFileName = "flocon_mocks_$formattedDate.json", dialogName = "Export mocks")

        if (selectedFile != null) {
            try {
                selectedFile.writeText(jsonString) // Extension Kotlin pour écrire du texte
                return ExportResult.Success
            } catch (e: Exception) {
                Logger.e("Error writing mocks", e)
                return ExportResult.Failure(e)
            }
        } else {
            Logger.d("Exporting cancelled")
            return ExportResult.Cancelled
        }
    }
}

private fun showSaveFileDialog(dialogName: String, defaultFileName: String): File? {
    val parentFrame = Frame()
    val dialog = FileDialog(parentFrame, dialogName, FileDialog.SAVE).apply {
        file = defaultFileName
    }

    dialog.isVisible = true // Bloque jusqu'à ce que la boîte de dialogue soit fermée

    val file = dialog.file
    val directory = dialog.directory

    // Libérer la frame temporaire après utilisation
    parentFrame.dispose()

    return if (file != null && directory != null) {
        File(directory, file)
    } else {
        null
    }
}
