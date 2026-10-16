package io.github.openflocon.flocondesktop.features.network.mock.processor

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.serialization.json.Json
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
        val selectedFile = FileKit.pickFile(
            type = PickerType.File(listOf("json")),
            mode = PickerMode.Single,
            title = "Importer les Mocks JSON",
        )

        if (selectedFile == null) {
            Logger.d("Importing cancelled")
            return ImportResult.Cancelled
        }

        val file = File(selectedFile.path)
        val jsonString = try {
            file.readText()
        } catch (e: FileNotFoundException) {
            Logger.e("File not found during import", e)
            return ImportResult.Failure(e)
        } catch (e: Exception) {
            Logger.e("Error reading file during import", e)
            return ImportResult.Failure(e)
        }

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
