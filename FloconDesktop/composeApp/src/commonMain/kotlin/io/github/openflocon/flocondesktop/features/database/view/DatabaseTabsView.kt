package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabViewAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.buildMenu

@Composable
fun DatabaseTabsView(
    modifier: Modifier = Modifier,
    tabs: List<DatabaseTabState>,
    selected: DatabaseTabState?,
    onAction: (DatabaseTabViewAction) -> Unit,
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
                onAction = onAction,
            )
        }
    }
}


@Composable
private fun DatabaseTabItemView(
    state: DatabaseTabState,
    selected: DatabaseTabState?,
    onAction: (DatabaseTabViewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
    )
    val contentColor = if (state == selected) FloconTheme.colorPalette.onSecondary else FloconTheme.colorPalette.onPrimary
    ContextualView(
        items = remember(state) {
            buildMenu {
                item("Close") {
                    onAction(DatabaseTabViewAction.OnCloseClicked(state))
                }
                item("Close Other Tabs") {
                    onAction(DatabaseTabViewAction.OnCloseOtherClicked(state))
                }
                item("Close All Tabs") {
                    onAction(DatabaseTabViewAction.OnCloseAllClicked(state))
                }
                item("Close Tabs to the Left") {
                    onAction(DatabaseTabViewAction.OnCloseOnLeftClicked(state))
                }
                item("Close Tabs to the Right") {
                    onAction(DatabaseTabViewAction.OnCloseOnRightClicked(state))
                }
                //item("Rename") {
                //
                //}
            }
        }
    ) {
        Row(
            modifier = modifier.clip(
                shape
            ).background(
                color = if (state == selected) FloconTheme.colorPalette.secondary else FloconTheme.colorPalette.primary
            ).clickable {
                onAction(DatabaseTabViewAction.OnTabSelected(state))
            }.padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                state.displayName, style = FloconTheme.typography.bodyMedium,
                color = contentColor,
            )
            Box(modifier = Modifier.clickable {
                onAction(DatabaseTabViewAction.OnCloseClicked(state))
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
}
