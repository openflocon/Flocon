package io.github.openflocon.domain.table.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.table.models.TableDomainModel
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportTableToCsvUseCase(
    private val observeCurrentDeviceTableContentUseCase: ObserveCurrentDeviceTableContentUseCase,
) {
    suspend operator fun invoke(): Either<Throwable, String> {
        val items = observeCurrentDeviceTableContentUseCase().firstOrNull() ?: return Failure(
            Throwable("no table to export")
        )

        val fileName = "table_${System.currentTimeMillis()}.csv"
        val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"
        val file = File(desktopPath, fileName)
        items.exportToCsv(file = file)
        return Success(file.absolutePath)
    }
}

private fun TableDomainModel.exportToCsv(file: File) {
    val headerList = buildList {
        add("date")
        addAll(columns)
    }
    file.writeText(headerList.joinToString(separator = ",", postfix = "\n"))

    items.forEach { item ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val startDate = Date(item.createdAt)
        val formattedTime = dateFormat.format(startDate)

        val dataList = listOf(
            "\"$formattedTime\"",
        ) + item.values.map {
            "\"$it\""
        }
        val escapedDataList = dataList.map { csvEscape(it) }
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
