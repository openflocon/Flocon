package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_tab_freeform
import flocondesktop.composeapp.generated.resources.deeplink_variables
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeeplinkTabs(
    tabs: List<DeeplinkTabUiModel>,
    selected: DeeplinkTabUiModel,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // a new or selected tab can be out of the visible area
    LaunchedEffect(selected.id, tabs.size) {
        val index = tabs.indexOfFirst { it.id == selected.id }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(tabs, key = { it.id }) { tab ->
            DeeplinkTabItem(
                tab = tab,
                isSelected = tab.id == selected.id,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun DeeplinkTabItem(
    tab: DeeplinkTabUiModel,
    isSelected: Boolean,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (isSelected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted
    val title = when (tab) {
        DeeplinkTabUiModel.Freeform -> stringResource(Res.string.deeplink_tab_freeform)
        DeeplinkTabUiModel.Variables -> stringResource(Res.string.deeplink_variables)
        is DeeplinkTabUiModel.Link -> tab.title
    }

    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(if (isSelected) FloconTheme.colors.bg100 else Color.Transparent)
            .clickable { onAction(DeeplinkScreenAction.SelectTab(tab.id)) }
            .padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .clickable { onAction(DeeplinkScreenAction.CloseTab(tab.id)) }
                .size(20.dp)
                .padding(all = FloconTheme.spacing.tiny)
        ) {
            Image(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                colorFilter = ColorFilter.tint(contentColor),
            )
        }
    }
}
