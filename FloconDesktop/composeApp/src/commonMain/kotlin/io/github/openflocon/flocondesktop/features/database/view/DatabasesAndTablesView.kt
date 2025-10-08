package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.composeunstyled.Text
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DatabasesAndTablesView(
    modifier: Modifier,
    state: DatabasesStateUiModel,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    onDatabaseDoubleClicked: (DeviceDataBaseId) -> Unit,
    onDatabaseSelected: (id: DeviceDataBaseId) -> Unit,
) {
    Surface(
        color = FloconTheme.colorPalette.primary,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .horizontalScroll(rememberScrollState())
    ) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(all = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Databases",
                color = FloconTheme.colorPalette.onSurface,
                style = FloconTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            when (state) {
                DatabasesStateUiModel.Empty -> Unit
                DatabasesStateUiModel.Loading -> Unit
                is DatabasesStateUiModel.WithContent -> {
                    state.databases.forEach {
                        DatabaseItemView(
                            state = it,
                            onSelect = onDatabaseSelected,
                            modifier = Modifier.fillMaxWidth(),
                            onTableDoubleClicked = onTableDoubleClicked,
                            onDatabaseDoubleClicked = onDatabaseDoubleClicked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseItemView(
    state: DeviceDataBaseUiModel,
    onSelect: (id: DeviceDataBaseId) -> Unit,
    onTableDoubleClicked: (TableUiModel) -> Unit,
    onDatabaseDoubleClicked: (DeviceDataBaseId) -> Unit,
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
                    onDatabaseDoubleClicked(state.id)
                }
            ).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.width(14.dp),
                imageVector = Icons.Outlined.Dataset,
                contentDescription = null,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(textColor),
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
                    )
                }
            }
        }
    }
}

@Composable
fun TableItemView(
    item: TableUiModel,
    modifier: Modifier = Modifier,
    onTableDoubleClicked: (TableUiModel) -> Unit,
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
                    ColumnView(it, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ColumnView(
    model: TableUiModel.ColumnUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(start = 46.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val color = FloconTheme.colorPalette.onSurface
        Image(
            imageVector = Icons.Outlined.ViewColumn,
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(color),
            contentDescription = null,
        )
        Text(
            model.name,
            style = FloconTheme.typography.bodyMedium,
            color = color,
        )

        Text(
            model.type,
            style = FloconTheme.typography.bodyMedium,
            color = color.copy(alpha = 0.6f),
        )
    }
}
