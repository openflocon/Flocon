package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel

@Composable
internal fun CrashReportDetailView(
    modifier: Modifier,
    crash: CrashReporterSelectedUiModel
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = crash.exceptionType,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = crash.dateFormatted,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = crash.exceptionMessage,
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider()
        Text(crash.stackTrace, style = MaterialTheme.typography.bodyMedium)
    }
}
