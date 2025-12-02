package io.github.openflocon.flocondesktop.features.network.mock.processor

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import kotlinx.serialization.json.Json
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FileNotFoundException

sealed interface ImportResult {
    data class Success(val mocks: List<MockNetworkDomainModel>) : ImportResult
    object Cancelled : ImportResult
    data class Failure(val error: Throwable) : ImportResult
}

class ImportMocksProcessor {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend operator fun invoke(): ImportResult {
        val selectedFile = showOpenFileDialog(dialogName = "Importer les Mocks JSON")

        if (selectedFile == null) {
            Logger.d("Importing cancelled")
            return ImportResult.Cancelled
        }

        val jsonString = try {
            selectedFile.readText() // Extension Kotlin pour lire le texte
        } catch (e: FileNotFoundException) {
            Logger.e("File not found during import", e)
            return ImportResult.Failure(e)
        } catch (e: Exception) {
            Logger.e("Error reading file during import", e)
            return ImportResult.Failure(e)
        }

        // 3. Désérialiser et mapper vers le DomainModel
        val domainMocks = try {
            val exportedMocks: List<MockNetworkExportedModel> = json.decodeFromString(jsonString)

            exportedMocks.map { it.toDomainModel() }
        } catch (e: Exception) {
            Logger.e("Error deserializing or mapping mocks", e)
            return ImportResult.Failure(e)
        }

        return ImportResult.Success(domainMocks)
    }
}

private fun showOpenFileDialog(dialogName: String): File? {
    val parentFrame = Frame()
    val dialog = FileDialog(parentFrame, dialogName, FileDialog.LOAD).apply {
        filenameFilter = java.io.FilenameFilter { _, name -> name.endsWith(".json", ignoreCase = true) }
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
