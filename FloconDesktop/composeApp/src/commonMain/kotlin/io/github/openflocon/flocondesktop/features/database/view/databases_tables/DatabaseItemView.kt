package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DatabaseItemView(
    state: DeviceDataBaseUiModel,
    onSelect: (id: DeviceDataBaseId) -> Unit,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    onDatabaseDoubleClicked: (DeviceDataBaseUiModel) -> Unit,
    onTableColumnClicked: (TableUiModel.ColumnUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (background, textColor) = if (state.isSelected) {
        FloconTheme.colorPalette.accent.copy(alpha = 0.4f) to FloconTheme.colorPalette.onAccent
    } else {
        Color.Transparent to FloconTheme.colorPalette.onSurface
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .combinedClickable(onClick = {
                    onSelect(state.id)
                }, onDoubleClick = {
                    onDatabaseDoubleClicked(state)
                }
                ).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.width(14.dp),
                imageVector = Icons.Outlined.Dataset,
                contentDescription = null,
                colorFilter = ColorFilter.tint(textColor),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                state.name,
                color = textColor,
                style = FloconTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        state.tables?.let { tables ->
            Column(modifier = Modifier.fillMaxWidth()) {
                tables.fastForEach {
                    TableItemView(
                        item = it,
                        modifier = Modifier.fillMaxWidth(),
                        onTableDoubleClicked = onTableDoubleClicked,
                        onTableColumnClicked = onTableColumnClicked,
                    )
                }
            }
        }
    }
}