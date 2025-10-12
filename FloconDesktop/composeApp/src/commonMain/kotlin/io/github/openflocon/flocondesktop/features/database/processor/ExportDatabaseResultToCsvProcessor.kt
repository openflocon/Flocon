package io.github.openflocon.flocondesktop.features.database.processor

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class ExportDatabaseResultToCsvProcessor {

    suspend operator fun invoke(
        columns: List<String>,
        values: List<List<String>>,
    ): Either<Throwable, String> {
        val fileName = "query_result_${System.currentTimeMillis()}.csv"

        val file = showSaveFileDialog(defaultFileName = fileName, dialogName = "Export query result as CSV") ?: return Failure(
            Throwable("no file selected")
        )

        exportToCsv(
            file = file,
            columns = columns,
            values = values,
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

        // Libérer la frame temporaire après utilisation
        parentFrame.dispose()

        return if (file != null && directory != null) {
            File(directory, file)
        } else {
            null
        }
    }
}

private fun exportToCsv(
    file: File,
    columns: List<String>,
    values: List<List<String>>,
) {
    val headerList = columns
    file.writeText(headerList.joinToString(separator = ",", postfix = "\n"))

    values.forEach { item ->
        val escapedDataList = item.map { csvEscape(it) }
        file.appendText(escapedDataList.joinToString(separator = ",", postfix = "\n"))
    }
}

private fun csvEscape(text: String?): String {
    val nonNullText = text ?: ""
    val containsSpecialChars = nonNullText.contains(',')
    return if (containsSpecialChars) {
        val escapedText = nonNullText.replace("\"", "\"\"")
        "\"$escapedText\""
    } else {
        nonNullText
    }
}
