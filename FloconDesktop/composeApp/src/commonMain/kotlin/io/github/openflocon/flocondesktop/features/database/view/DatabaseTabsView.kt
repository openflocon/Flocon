package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DatabaseTabsView(
    modifier: Modifier = Modifier,
    tabs: List<DatabaseTabState>,
    selected: DatabaseTabState?,
    onTabSelected: (DatabaseTabState) -> Unit,
    onCloseClicked: (DatabaseTabState) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        items(tabs) {
            DatabaseTabItemView(
                state = it,
                selected = selected,
                onTabSelected = onTabSelected,
                onCloseClicked = onCloseClicked,
            )
        }
    }
}

@Composable
private fun DatabaseTabItemView(
    state: DatabaseTabState,
    selected: DatabaseTabState?,
    onTabSelected: (DatabaseTabState) -> Unit,
    onCloseClicked: (DatabaseTabState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (state == selected) FloconTheme.colorPalette.onAccent else FloconTheme.colorPalette.onPrimary
    Row(
        modifier = modifier.clip(
            RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 6.dp,
            )
        ).background(
            color = if (state == selected) FloconTheme.colorPalette.accent else FloconTheme.colorPalette.primary
        ).clickable {
            onTabSelected(state)
        }.padding(vertical = 4.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            state.name, style = FloconTheme.typography.bodyMedium,
            color = contentColor,
        )
        Box(modifier = Modifier.clickable {
            onCloseClicked(state)
        }.size(20.dp).padding(all = 2.dp)) {
            Image(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(contentColor),
            )
        }
    }
}
