package io.github.openflocon.flocondesktop.features.table.view

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
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.table.model.TableAction
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.previewTableRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TableRowView(
    model: TableRowUiModel,
    columnsWidth: Dp = 150.dp,
    onAction: (action: TableAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    ContextualView(
        items = contextualActions(
            onAction = onAction,
            state = model,
        ),
    ) {
        SelectionContainer {
            Row(
                modifier = modifier
                    .clip(FloconTheme.shapes.medium)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                model.values.fastForEach { column ->
                    Text(
                        text = column,
                        modifier =
                            Modifier
                                .width(columnsWidth)
                                .padding(horizontal = 4.dp),
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
                        color = FloconTheme.colorPalette.onPrimary,
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
): List<FloconContextMenuItem> {
    val onActionCallback by rememberUpdatedState(onAction)
    return remember(state) {
        buildList {
            add(
                FloconContextMenuItem.Item(
                    label = "Remove",
                    onClick = {
                        onActionCallback(TableAction.Remove(state))
                    }
                ),
            )
            add(
                FloconContextMenuItem.Item(
                    label = "Remove lines above ",
                    onClick = {
                        onActionCallback(TableAction.RemoveLinesAbove(state))
                    }
                ),
            )
        }
    }
}


@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        TableRowView(
            model = previewTableRowUiModel(),
            onAction = {},
        )
    }
}
