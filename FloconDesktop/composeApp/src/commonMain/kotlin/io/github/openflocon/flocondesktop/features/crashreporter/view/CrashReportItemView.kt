package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_delete
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.previewCrashReportItem
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CrashReportItemView(
    crash: CrashReporterUiModel,
    isSelected: Boolean,
    onClick: (CrashReporterUiModel) -> Unit,
    onDelete: (CrashReporterUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualView(
        items = contextualActions(
            onDelete = {
                onDelete(crash)
            },
        ),
    ) {
        Column(
            modifier = modifier
                .clickable {
                    onClick(crash)
                }
                .clip(FloconTheme.shapes.medium)
                .background(
                    if (isSelected) {
                        FloconTheme.colorPalette.accent
                    } else {
                        FloconTheme.colorPalette.surface
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = crash.exceptionType,
                style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun contextualActions(
    onDelete: () -> Unit,
): List<FloconContextMenuItem> {
    val onDeleteCallback by rememberUpdatedState(onDelete)
    val deleteString = stringResource(Res.string.general_delete)
    return remember {
        buildList {
            add(
                FloconContextMenuItem.Item(
                    label = deleteString,
                    onClick = {
                        onDeleteCallback()
                    }
                ),
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
            isSelected = false,
            onDelete = {},
        )
    }
}
