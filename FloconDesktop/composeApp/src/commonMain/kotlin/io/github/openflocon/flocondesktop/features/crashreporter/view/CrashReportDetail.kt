package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton

/** The header of a crash, then its body: used where there is no top bar to hold the header (the crash panel). */
@Composable
internal fun CrashReportDetail(
    onCopy: (CrashReporterSelectedUiModel) -> Unit,
    onDelete: (CrashReporterSelectedUiModel) -> Unit,
    crash: CrashReporterSelectedUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(
                top = FloconTheme.spacing.medium,
                start = FloconTheme.spacing.large,
                end = FloconTheme.spacing.large,
            ),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            SelectionContainer(Modifier.weight(1f)) {
                Column {
                    Text(
                        text = crash.exceptionType,
                        style = FloconTheme.typography.titleMedium,
                        color = FloconTheme.colors.statusError
                    )
                    Text(
                        text = crash.dateFormatted,
                        style = FloconTheme.typography.bodyMedium
                    )
                    Text(
                        text = crash.exceptionMessage,
                        style = FloconTheme.typography.bodyMedium
                    )
                }
            }
            FloconIconButton(onClick = { onDelete(crash) }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
            FloconIconButton(onClick = { onCopy(crash) }) {
                Icon(Icons.Filled.CopyAll, contentDescription = null)
            }
        }
        CrashReportBody(
            stackTrace = crash.stackTrace,
            modifier = Modifier.weight(1f),
        )
    }
}
