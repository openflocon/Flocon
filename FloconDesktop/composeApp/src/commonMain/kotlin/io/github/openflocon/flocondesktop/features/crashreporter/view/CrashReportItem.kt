package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_delete
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.previewCrashReportItem
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconDot
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CrashReportItem(
    crash: CrashReporterUiModel,
    isSelected: Boolean,
    onClick: (CrashReporterUiModel) -> Unit,
    onDelete: (CrashReporterUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualMenu(
        items = contextualActions(
            onDelete = {
                onDelete(crash)
            },
        ),
    ) {
        Row(
            modifier = modifier
                .clip(FloconTheme.shapes.medium)
                .background(if (isSelected) FloconTheme.colors.accentSoft else Color.Transparent)
                .clickable { onClick(crash) }
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconDot(color = FloconTheme.colors.statusError)
            Column(Modifier.weight(1f)) {
                Text(
                    text = crash.exceptionType,
                    style = FloconTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = crash.exceptionMessage,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = crash.timeFormatted,
                style = FloconTheme.typography.monoSmall,
                color = FloconTheme.colors.inkMuted,
            )
        }
    }
}

@Composable
private fun contextualActions(
    onDelete: () -> Unit,
): ImmutableList<FloconContextMenuItem> {
    val onDeleteCallback by rememberUpdatedState(onDelete)
    val deleteString = stringResource(Res.string.general_delete)
    return remember {
        persistentListOf(
            FloconContextMenuItem.Item(
                label = deleteString,
                onClick = {
                    onDeleteCallback()
                }
            ),
        )
    }
}

@Preview
@Composable
private fun CrashReportItemViwPreview() {
    FloconTheme {
        CrashReportItem(
            crash = previewCrashReportItem(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            isSelected = false,
            onDelete = {},
        )
    }
}
