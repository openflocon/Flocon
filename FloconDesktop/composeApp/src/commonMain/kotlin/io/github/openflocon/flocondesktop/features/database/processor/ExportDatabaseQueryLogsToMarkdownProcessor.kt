package io.github.openflocon.flocondesktop.features.database.processor

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportDatabaseQueryLogsToMarkdownProcessor {

    suspend operator fun invoke(
        logs: List<DatabaseQueryLogDomainModel>
    ): Either<Throwable, String> {
        val fileName = "database_logs_${System.currentTimeMillis()}.md"

        val file = showSaveFileDialog(defaultFileName = fileName, dialogName = "Export database logs as Markdown") ?: return Failure(
            Throwable("no file selected")
        )

        exportToMarkdown(
            file = file,
            logs = logs,
        )
        return Success(file.absolutePath)
    }

    private fun showSaveFileDialog(dialogName: String, defaultFileName: String): File? {
        val parentFrame = Frame()
        val dialog = FileDialog(parentFrame, dialogName, FileDialog.SAVE).apply {
            file = defaultFileName
        }

        dialog.isVisible = true 

        val file = dialog.file
        val directory = dialog.directory

        parentFrame.dispose()

        return if (file != null && directory != null) {
            File(directory, file)
        } else {
            null
        }
    }

    private fun exportToMarkdown(
        file: File,
        logs: List<DatabaseQueryLogDomainModel>,
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val markdown = buildString {
            appendLine("# Database Query Logs")
            appendLine()
            appendLine("| Date | SQL Query | Arguments |")
            appendLine("| --- | --- | --- | --- |")

            logs.forEach { log ->
                val date = dateFormat.format(Date(log.timestamp))
                val sql = log.sqlQuery.replace("|", "\\|").replace("\n", " ")
                val args = (log.bindArgs?.toString() ?: "[]").replace("|", "\\|")
                
                appendLine("| $date | `$sql` | `$args` |")
            }
        }
        
        file.writeText(markdown)
    }
}
