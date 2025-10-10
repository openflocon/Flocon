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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.composeunstyled.Text
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun TableItemView(
    item: TableUiModel,
    modifier: Modifier = Modifier,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    onTableColumnClicked: (TableUiModel.ColumnUiModel) -> Unit,
) {
    var isOpened by remember(item.name) { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .combinedClickable(
                    onClick = {
                        isOpened = !isOpened
                    },
                    onDoubleClick = {
                        onTableDoubleClicked(item)
                    }
                )
                .padding(horizontal = 12.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val color = FloconTheme.colorPalette.onSurface
            Image(
                imageVector = Icons.Outlined.ChevronRight,
                modifier = Modifier.size(14.dp).graphicsLayer {
                    rotationZ = if (isOpened) 90f else 0f
                },
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
            )
            Image(
                imageVector = Icons.Outlined.TableRows,
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(color),
                contentDescription = null,
            )
            Text(
                item.name,
                style = FloconTheme.typography.bodyMedium,
                color = color,
            )
        }
        AnimatedVisibility(isOpened, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                item.columns.fastForEach {
                    ColumnView(
                        model = it,
                        onClicked = onTableColumnClicked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}