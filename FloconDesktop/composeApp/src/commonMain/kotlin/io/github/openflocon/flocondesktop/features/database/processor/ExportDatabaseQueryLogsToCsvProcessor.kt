package io.github.openflocon.flocondesktop.features.database.processor

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.toFullSql
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportDatabaseQueryLogsToCsvProcessor {

    suspend operator fun invoke(
        logs: List<DatabaseQueryLogDomainModel>
    ): Either<Throwable, String> {
        val baseName = "database_logs_${System.currentTimeMillis()}"

        val selectedFile = FileKit.openFileSaver(
            suggestedName = baseName,
            defaultExtension = "csv",
        ) ?: return Failure(Throwable("no file selected"))

        val file = File(selectedFile.path)
        exportToCsv(
            file = file,
            logs = logs,
        )
        return Success(file.absolutePath)
    }

    private fun exportToCsv(
        file: File,
        logs: List<DatabaseQueryLogDomainModel>,
    ) {
        val columns = listOf("Date", "SQL Query", "Arguments", "Full SQL")
        file.writeText(columns.joinToString(separator = ",", postfix = "\n"))

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        logs.forEach { log ->
            val row = listOf(
                dateFormat.format(Date(log.timestamp)),
                log.sqlQuery,
                log.bindArgs?.toString() ?: "[]",
                log.toFullSql()
            )
            val escapedRow = row.map { csvEscape(it) }
            file.appendText(escapedRow.joinToString(separator = ",", postfix = "\n"))
        }
    }

    private fun csvEscape(text: String?): String {
        val nonNullText = text ?: ""
        val containsSpecialChars = nonNullText.contains(',') || nonNullText.contains('"') || nonNullText.contains('\n')
        return if (containsSpecialChars) {
            val escapedText = nonNullText.replace("\"", "\"\"")
            "\"$escapedText\""
        } else {
            nonNullText
        }
    }
}
