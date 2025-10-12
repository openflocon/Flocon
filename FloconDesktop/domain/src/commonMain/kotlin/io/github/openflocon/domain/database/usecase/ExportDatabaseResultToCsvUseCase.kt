package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Success
import java.io.File

class ExportDatabaseResultToCsvUseCase {

    suspend operator fun invoke(
        columns: List<String>,
        values: List<List<String>>,
    ): Either<Throwable, String> {
        val fileName = "query_result_${System.currentTimeMillis()}.csv"
        val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"
        val file = File(desktopPath, fileName)
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
