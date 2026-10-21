package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.ContextualView
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabViewAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.buildMenu
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.close
import flocondesktop.composeapp.generated.resources.general_rename
import flocondesktop.composeapp.generated.resources.tabs_close_all
import flocondesktop.composeapp.generated.resources.tabs_close_left
import flocondesktop.composeapp.generated.resources.tabs_close_others
import flocondesktop.composeapp.generated.resources.tabs_close_right

private val TabIndicatorHeight = 2.dp

@Composable
fun DatabaseTabsView(
    modifier: Modifier = Modifier,
    tabs: ImmutableList<DatabaseTabState>,
    selected: DatabaseTabState?,
    onAction: (DatabaseTabViewAction) -> Unit,
) {
    val lineColor = FloconTheme.colors.line
    LazyRow(
        modifier = modifier.drawBehind {
            drawRect(
                color = lineColor,
                topLeft = Offset(0f, size.height - 1.dp.toPx()),
                size = Size(size.width, 1.dp.toPx()),
            )
        },
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
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
    val contentColor = if (state == selected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted
    // the labels are resolved here: the menu is built outside of the composition
    val close = stringResource(Res.string.close)
    val closeOthers = stringResource(Res.string.tabs_close_others)
    val closeAll = stringResource(Res.string.tabs_close_all)
    val closeLeft = stringResource(Res.string.tabs_close_left)
    val closeRight = stringResource(Res.string.tabs_close_right)
    ContextualView(
        items = remember(state, close) {
            buildMenu {
                item(close) {
                    onAction(DatabaseTabViewAction.OnCloseClicked(state))
                }
                item(closeOthers) {
                    onAction(DatabaseTabViewAction.OnCloseOtherClicked(state))
                }
                item(closeAll) {
                    onAction(DatabaseTabViewAction.OnCloseAllClicked(state))
                }
                item(closeLeft) {
                    onAction(DatabaseTabViewAction.OnCloseOnLeftClicked(state))
                }
                item(closeRight) {
                    onAction(DatabaseTabViewAction.OnCloseOnRightClicked(state))
                }
                // item(stringResource(Res.string.general_rename)) {
                //
                // }
            }
        }
    ) {
        val indicatorColor = FloconTheme.colors.accent
        Row(
            modifier = modifier
                .clip(shape)
                .clickable {
                    onAction(DatabaseTabViewAction.OnTabSelected(state))
                }
                .drawBehind {
                    if (state == selected) {
                        drawRect(
                            color = indicatorColor,
                            topLeft = Offset(0f, size.height - TabIndicatorHeight.toPx()),
                            size = Size(size.width, TabIndicatorHeight.toPx()),
                        )
                    }
                }
                .padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                state.displayName,
                style = FloconTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            )
            Box(
                modifier = Modifier.clickable {
                    onAction(DatabaseTabViewAction.OnCloseClicked(state))
                }.size(20.dp).padding(all = FloconTheme.spacing.tiny)
            ) {
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
