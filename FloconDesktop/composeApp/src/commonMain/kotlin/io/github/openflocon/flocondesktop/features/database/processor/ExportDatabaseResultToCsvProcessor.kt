package io.github.openflocon.flocondesktop.features.database.processor

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.vinceglb.filekit.core.FileKit
import java.io.File

class ExportDatabaseResultToCsvProcessor {

    suspend operator fun invoke(
        columns: List<String>,
        values: List<List<String>>,
    ): Either<Throwable, String> {
        val baseName = "query_result_${System.currentTimeMillis()}"

        val selectedFile = FileKit.saveFile(
            baseName = baseName,
            extension = "csv",
        ) ?: return Failure(Throwable("no file selected"))

        val file = selectedFile.file
        exportToCsv(
            file = file,
            columns = columns,
            values = values,
        )
        return Success(file.absolutePath)
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
