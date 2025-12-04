package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.previewCrashReportItem
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CrashReportItemView(
    crash: CrashReporterUiModel,
    onClick: (CrashReporterUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable{
            onClick(crash)
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = crash.exceptionType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = crash.dateFormatted,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = crash.exceptionMessage,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun CrashReportItemViwPreview() {
    FloconTheme {
        CrashReportItemView(
            crash = previewCrashReportItem(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
        )
    }
}