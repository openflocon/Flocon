package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatabaseQueryToolbarView(
    favoritesTitles: Set<String>,
    onAction: (action: DatabaseTabAction) -> Unit,
    isQueryEmpty: Boolean,
    lastQueries: List<String>,
    modifier: Modifier = Modifier,
) {
    var showFavoriteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .height(40.dp)
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable(enabled = isQueryEmpty.not()) {
                    showFavoriteDialog = true
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
                    .graphicsLayer(
                        alpha = if (isQueryEmpty) 0.6f else 1f
                    ),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))

        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable(enabled = isQueryEmpty.not()) {
                    onAction(DatabaseTabAction.Copy)
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.CopyAll,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
                    .graphicsLayer(
                        alpha = if (isQueryEmpty) 0.6f else 1f
                    ),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }

        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))

        var isHistoryExpanded by remember { mutableStateOf(false) }
        val displayOldQueries = isHistoryExpanded && lastQueries.isNotEmpty()

        FloconExposedDropdownMenuBox(
            expanded = displayOldQueries,
            onExpandedChange = { isHistoryExpanded = false },
        ) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(2.dp))
                    .clickable(enabled = lastQueries.isNotEmpty()) {
                        isHistoryExpanded = true
                    }.aspectRatio(1f, true),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
                )
            }

            FloconExposedDropdownMenu(
                expanded = displayOldQueries,
                onDismissRequest = { isHistoryExpanded = false },
                modifier = Modifier.width(300.dp)
            ) {
                lastQueries.fastForEach { query ->
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                            onAction(DatabaseTabAction.ExecuteQuery(query))
                            isHistoryExpanded = false
                        },
                        text = query,
                        style = FloconTheme.typography.bodySmall,
                    )
                }
            }
        }

        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                onAction(DatabaseTabAction.Import)
            }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Outlined.FileOpen,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                onAction(DatabaseTabAction.ClearQuery)
            }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
    }

    if (showFavoriteDialog) {
        SaveFavoriteDialog(
            favoritesTitles = favoritesTitles,
            onDismiss = { showFavoriteDialog = false },
            onSave = { queryName ->
                onAction(DatabaseTabAction.SaveFavorite(queryName))
                showFavoriteDialog = false
            }
        )
    }
}
