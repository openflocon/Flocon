@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_see_all_queries
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.WithTooltip
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseItem(
    state: DeviceDataBaseUiModel,
    onSelect: (id: DeviceDataBaseId) -> Unit,
    onTableDoubleClicked: (databaseId: DeviceDataBaseId, TableUiModel) -> Unit,
    onDatabaseDoubleClicked: (DeviceDataBaseUiModel) -> Unit,
    onTableColumnClicked: (TableUiModel.ColumnUiModel) -> Unit,
    onDeleteContentClicked: (databaseId: DeviceDataBaseId, TableUiModel) -> Unit,
    onInsertContentClicked: (databaseId: DeviceDataBaseId, TableUiModel) -> Unit,
    onSeeAllQueriesClicked: (DeviceDataBaseId, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupBackground = if (state.isSelected) {
        FloconTheme.colors.ink.copy(alpha = 0.05f)
    } else {
        Color.Transparent
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(groupBackground),
    ) {
        DatabaseEntry(
            modifier = Modifier.fillMaxWidth(),
            onSelect = onSelect,
            state = state,
            onDatabaseDoubleClicked = onDatabaseDoubleClicked,
            onSeeAllQueriesClicked = onSeeAllQueriesClicked,
        )
        state.tables?.let { tables ->
            val guideColor = FloconTheme.colors.line
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = FloconTheme.spacing.extraLarge, end = FloconTheme.spacing.extraSmall, bottom = FloconTheme.spacing.small)
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
                tables.fastForEach {
                    TableEntry(
                        item = it,
                        modifier = Modifier.fillMaxWidth(),
                        onTableDoubleClicked = {
                            onTableDoubleClicked(state.id, it)
                        },
                        onTableColumnClicked = onTableColumnClicked,
                        onDeleteContentClicked = {
                            onDeleteContentClicked(state.id, it)
                        },
                        onInsertContentClicked = {
                            onInsertContentClicked(state.id, it)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DatabaseEntry(
    onSelect: (DeviceDataBaseId) -> Unit,
    state: DeviceDataBaseUiModel,
    onDatabaseDoubleClicked: (DeviceDataBaseUiModel) -> Unit,
    onSeeAllQueriesClicked: (DeviceDataBaseId, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = FloconTheme.colors.ink
    val iconColor = if (state.isSelected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted

    ContextualMenu(
        persistentListOf(
            FloconContextMenuItem.Item(
                label = stringResource(Res.string.database_see_all_queries),
                onClick = {
                    onSeeAllQueriesClicked(state.id, state.name)
                }
            )
        )
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .combinedClickable(
                    onClick = {
                        onSelect(state.id)
                    },
                    onDoubleClick = {
                        onDatabaseDoubleClicked(state)
                    }
                ).padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(14.dp).graphicsLayer {
                    rotationZ = if (state.isSelected) 90f else 0f
                },
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(FloconTheme.colors.inkMuted),
            )
            Spacer(modifier = Modifier.width(FloconTheme.spacing.small))
            Image(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Outlined.Dataset,
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
            )
            Spacer(modifier = Modifier.width(FloconTheme.spacing.small))
            Text(
                state.name,
                color = textColor,
                style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            WithTooltip("See logs") {
                Box(
                    modifier = Modifier.width(20.dp)
                        .clickable(onClick = {
                            onSeeAllQueriesClicked(state.id, state.name)
                        })
                ) {
                    Image(
                        modifier = Modifier.width(16.dp),
                        imageVector = Icons.Outlined.Preview,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(iconColor),
                    )
                }
            }
        }
    }
}
