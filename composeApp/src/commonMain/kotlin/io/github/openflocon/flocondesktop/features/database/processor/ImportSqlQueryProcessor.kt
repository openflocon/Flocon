package io.github.openflocon.flocondesktop.features.database.processor

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.path
import java.io.File

class ImportSqlQueryProcessor {

    suspend operator fun invoke(): String? {
        val selectedFile = FileKit.openFilePicker(
            type = FileKitType.File("sql", "txt"),
        ) ?: return null

        return File(selectedFile.path).readText()
    }
}
