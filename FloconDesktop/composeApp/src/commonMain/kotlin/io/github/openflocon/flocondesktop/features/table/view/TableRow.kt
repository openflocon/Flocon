package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.remove_lines_above
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.table.model.TableAction
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.previewTableRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun TableRow(
    model: TableRowUiModel,
    columnsWidth: Dp = 150.dp,
    onAction: (action: TableAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualMenu(
        items = contextualActions(
            onAction = onAction,
            state = model,
        ),
    ) {
        SelectionContainer {
            Row(
                modifier = modifier
                    .clip(FloconTheme.shapes.medium)
                    .clickable(onClick = { onAction(TableAction.OnClick(model)) })
                    .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
            ) {
                model.values.fastForEach { column ->
                    Text(
                        text = column,
                        modifier =
                        Modifier
                            .width(columnsWidth)
                            .padding(horizontal = FloconTheme.spacing.extraSmall),
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
                        color = FloconTheme.colors.ink,
                    )
                }
            }
        }
    }
}

@Composable
private fun contextualActions(
    onAction: (TableAction) -> Unit,
    state: TableRowUiModel
): ImmutableList<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    val removeString = stringResource(Res.string.remove)
    val removeAboveString = stringResource(Res.string.remove_lines_above)
    return remember(state) {
        persistentListOf(
            FloconContextMenuItem.Item(
                label = removeString,
                onClick = {
                    onActionCallback(TableAction.Remove(state))
                }
            ),
            FloconContextMenuItem.Item(
                label = removeAboveString,
                onClick = {
                    onActionCallback(TableAction.RemoveLinesAbove(state))
                }
            ),
        )
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        TableRow(
            model = previewTableRowUiModel(),
            onAction = {},
        )
    }
}
