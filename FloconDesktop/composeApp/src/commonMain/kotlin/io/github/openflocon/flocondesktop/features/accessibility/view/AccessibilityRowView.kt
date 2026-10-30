package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_column_class
import flocondesktop.composeapp.generated.resources.accessibility_column_text
import flocondesktop.composeapp.generated.resources.accessibility_column_type
import flocondesktop.composeapp.generated.resources.accessibility_missing_label
import flocondesktop.composeapp.generated.resources.time
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityAction
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityRowUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.previewAccessibilityRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import org.jetbrains.compose.resources.stringResource

private val TimeWidth = 110.dp
private val TypeWidth = 160.dp
private val ClassNameWidth = 240.dp

@Composable
fun AccessibilityRowView(
    model: AccessibilityRowUiModel,
    onAction: (AccessibilityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = { onAction(AccessibilityAction.OnClick(model)) })
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = model.timeFormatted,
            modifier = Modifier
                .width(TimeWidth)
                .padding(end = 12.dp),
            maxLines = 1,
            style = FloconTheme.typography.listItem.copy(fontWeight = FontWeight.Light),
            color = FloconTheme.colors.ink.copy(alpha = 0.4f),
        )
        Text(
            text = model.type,
            modifier = Modifier
                .width(TypeWidth)
                .padding(end = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.listItem.copy(fontWeight = FontWeight.Medium),
            color = FloconTheme.colors.ink,
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (model.missingLabel) {
                Text(
                    text = stringResource(Res.string.accessibility_missing_label),
                    modifier = Modifier.padding(end = 8.dp),
                    maxLines = 1,
                    style = FloconTheme.typography.listItem.copy(fontWeight = FontWeight.Medium),
                    color = FloconTheme.colors.statusWarning,
                )
            }
            Text(
                text = model.text,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.ink,
            )
        }
        Text(
            text = model.className.orEmpty(),
            modifier = Modifier.width(ClassNameWidth),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = FloconTheme.typography.listItem,
            color = FloconTheme.colors.ink.copy(alpha = 0.5f),
        )
    }
}

@Composable
fun AccessibilityHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .floconTableHeader()
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloconTableHeaderLabel(
            text = stringResource(Res.string.time),
            modifier = Modifier.width(TimeWidth).padding(end = FloconTheme.spacing.medium),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.accessibility_column_type),
            modifier = Modifier.width(TypeWidth).padding(end = FloconTheme.spacing.medium),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.accessibility_column_text),
            modifier = Modifier.weight(1f).padding(end = FloconTheme.spacing.medium),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.accessibility_column_class),
            modifier = Modifier.width(ClassNameWidth),
        )
    }
}

@Preview
@Composable
private fun AccessibilityRowViewPreview() {
    FloconTheme {
        AccessibilityRowView(
            model = previewAccessibilityRowUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
