package io.github.openflocon.flocondesktop.features.database.processor

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import java.io.File

class ImportSqlQueryProcessor {

    suspend operator fun invoke(): String? {
        val selectedFile = FileKit.pickFile(
            type = PickerType.File(listOf("sql", "txt")),
            mode = PickerMode.Single,
            title = "Import Sql Query",
        ) ?: return null

        return File(selectedFile.path).readText()
    }
}
