package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportAnalyticsToCsvUseCase(
    private val getCurrentDeviceAnalyticsContentUseCase: GetCurrentDeviceAnalyticsContentUseCase,
) {
    suspend operator fun invoke(
        filter: String?,
    ): Either<Throwable, String> {
        val items = getCurrentDeviceAnalyticsContentUseCase(
            filter = filter,
        ).takeIf { it.isNotEmpty() } ?: return Failure(
            Throwable("no analytics to export")
        )

        val baseName = "analytics_${System.currentTimeMillis()}"

        val selectedFile = FileKit.openFileSaver(
            suggestedName = baseName,
            defaultExtension = "csv",
        ) ?: return Failure(Throwable("no file selected"))

        val file = File(selectedFile.path)

        items.exportToCsv(file = file)
        return Success(file.absolutePath)
    }
}

private fun List<AnalyticsItemDomainModel>.exportToCsv(file: File) {
    val headerList = listOf(
        "name",
        "date",
    )
    file.writeText(headerList.joinToString(separator = ",", postfix = "\n"))

    this.forEach { item ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val startDate = Date(item.createdAt)
        val formattedTime = dateFormat.format(startDate)

        val dataList = listOf(
            "\"${item.eventName}\"",
            "\"$formattedTime\"",
        ) + item.properties.map { (name, value) ->
            "\"$name:$value\""
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
