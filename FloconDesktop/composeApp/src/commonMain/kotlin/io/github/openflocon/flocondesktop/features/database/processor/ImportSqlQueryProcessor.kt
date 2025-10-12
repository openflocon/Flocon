package io.github.openflocon.flocondesktop.features.database.processor

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class ImportSqlQueryProcessor {

    suspend operator fun invoke() : String? {
        return showOpenFileDialog(
                dialogName = "Import Sql Query"
            )?.readText()
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
