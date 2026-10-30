package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_insert_new_values
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.buildMenu
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TableEntry(
    item: TableUiModel,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    onTableColumnClicked: (TableUiModel.ColumnUiModel) -> Unit,
    onDeleteContentClicked: (TableUiModel) -> Unit,
    onInsertContentClicked: (TableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isOpened by remember(item.name) { mutableStateOf(false) }
    Column(modifier = modifier) {
        ContextualMenu(
            modifier = Modifier.fillMaxWidth(),
            items = remember(item) {
                buildMenu {
                    item("Insert new values") {
                        onInsertContentClicked(item)
                    }
                    item("Delete all data from ${item.name}") {
                        onDeleteContentClicked(item)
                    }
                }
            },
        ) {
            TableEntryContent(
                modifier = Modifier.fillMaxWidth(),
                isOpened = isOpened,
                onTableDoubleClicked = onTableDoubleClicked,
                item = item,
                updateIsOpened = {
                    isOpened = it
                }
            )
        }

        AnimatedVisibility(isOpened, modifier = Modifier.fillMaxWidth()) {
            val guideColor = FloconTheme.colors.line
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = FloconTheme.spacing.large, bottom = FloconTheme.spacing.extraSmall)
                    .drawBehind {
                        drawLine(
                            color = guideColor,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    .padding(start = FloconTheme.spacing.small),
            ) {
                item.columns.fastForEach {
                    ColumnItem(
                        model = it,
                        onClicked = onTableColumnClicked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TableEntryContent(
    isOpened: Boolean,
    updateIsOpened: (Boolean) -> Unit,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    item: TableUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    updateIsOpened(!isOpened)
                },
                onDoubleClick = {
                    onTableDoubleClicked(item)
                }
            )
            .padding(horizontal = FloconTheme.spacing.small)
            .padding(vertical = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        val color = FloconTheme.colors.ink
        Image(
            imageVector = Icons.Outlined.ChevronRight,
            modifier = Modifier.size(14.dp).graphicsLayer {
                rotationZ = if (isOpened) 90f else 0f
            },
            colorFilter = ColorFilter.tint(FloconTheme.colors.inkMuted),
            contentDescription = null,
        )
        Image(
            imageVector = Icons.Outlined.TableRows,
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(if (isOpened) color else FloconTheme.colors.inkMuted),
            contentDescription = null,
        )
        Text(
            item.name,
            style = FloconTheme.typography.bodyMedium,
            color = color,
        )
    }
}
