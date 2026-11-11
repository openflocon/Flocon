package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_fields_count
import flocondesktop.composeapp.generated.resources.deeplink_missing_variables
import flocondesktop.composeapp.generated.resources.deeplink_section_declared
import flocondesktop.composeapp.generated.resources.deeplink_section_recent
import flocondesktop.composeapp.generated.resources.deeplink_tab_freeform
import flocondesktop.composeapp.generated.resources.deeplink_variables
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.toTab
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSolidPill
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val EntryIconSize = 14.dp

// the background of the selected entry, as the tables of the database screen
private const val SelectedAlpha = 0.08f

@Composable
internal fun DeeplinkSidebar(
    deepLinks: List<DeeplinkViewState>,
    variablesCount: Int,
    selectedTabId: String,
    filter: String,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val declared = remember(deepLinks, filter) {
        deepLinks.filter { !it.isHistory && it.displayName.contains(filter, ignoreCase = true) }
    }
    val recent = remember(deepLinks, filter) {
        deepLinks.filter { it.isHistory && it.displayName.contains(filter, ignoreCase = true) }
    }
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
            ) {
                item(key = DeeplinkTabUiModel.Freeform.id) {
                    val isSelected = selectedTabId == DeeplinkTabUiModel.Freeform.id
                    SidebarEntry(
                        selected = isSelected,
                        onClick = { onAction(DeeplinkScreenAction.OpenTab(DeeplinkTabUiModel.Freeform)) },
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.Edit,
                            tint = if (isSelected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
                            modifier = Modifier.size(EntryIconSize),
                        )
                        SidebarTitle(
                            text = stringResource(Res.string.deeplink_tab_freeform),
                            selected = isSelected,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (declared.isNotEmpty()) {
                    item(key = "header-declared") {
                        SidebarSectionHeader(stringResource(Res.string.deeplink_section_declared), declared.size)
                    }
                    items(declared, key = { "declared-${it.deeplinkId}" }) { item ->
                        DeeplinkSidebarLink(item, selectedTabId, onAction)
                    }
                }
                if (recent.isNotEmpty()) {
                    item(key = "header-recent") {
                        SidebarSectionHeader(stringResource(Res.string.deeplink_section_recent), recent.size)
                    }
                    items(recent, key = { "recent-${it.deeplinkId}" }) { item ->
                        DeeplinkSidebarLink(item, selectedTabId, onAction)
                    }
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight().align(Alignment.TopEnd),
            )
        }
        if (variablesCount > 0) {
            val isSelected = selectedTabId == DeeplinkTabUiModel.Variables.id
            FloconHorizontalDivider()
            SidebarEntry(
                selected = isSelected,
                onClick = { onAction(DeeplinkScreenAction.OpenTab(DeeplinkTabUiModel.Variables)) },
                modifier = Modifier.padding(FloconTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(Res.string.deeplink_variables),
                    style = FloconTheme.typography.listItem,
                    fontWeight = FontWeight.SemiBold,
                    color = FloconTheme.colors.ink,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = variablesCount.toString(),
                    style = FloconTheme.typography.listItem,
                    color = FloconTheme.colors.inkMuted,
                )
                FloconIcon(
                    imageVector = Icons.Outlined.ChevronRight,
                    tint = FloconTheme.colors.inkMuted,
                    modifier = Modifier.size(EntryIconSize),
                )
            }
        }
    }
}

@Composable
private fun DeeplinkSidebarLink(
    item: DeeplinkViewState,
    selectedTabId: String,
    onAction: (DeeplinkScreenAction) -> Unit,
) {
    val tab = remember(item) { item.toTab() }
    val isSelected = tab.id == selectedTabId
    SidebarEntry(
        selected = isSelected,
        onClick = { onAction(DeeplinkScreenAction.OpenTab(tab)) },
    ) {
        if (item.isHistory) {
            Text(
                text = item.displayName,
                style = FloconTheme.typography.listItemMono,
                color = if (isSelected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            return@SidebarEntry
        }
        SidebarTitle(item.displayName, selected = isSelected, modifier = Modifier.weight(1f))
        when {
            item.missingVariablesCount > 0 -> FloconSolidPill(
                text = stringResource(Res.string.deeplink_missing_variables, item.missingVariablesCount),
                background = FloconTheme.colors.accent,
                contentColor = FloconTheme.colors.accentInk,
            )

            item.fieldsCount > 0 -> Text(
                text = pluralStringResource(Res.plurals.deeplink_fields_count, item.fieldsCount, item.fieldsCount),
                style = FloconTheme.typography.labelSmall,
                color = FloconTheme.colors.inkMuted,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SidebarEntry(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FloconTheme.sizes.rowHeight)
            .clip(FloconTheme.shapes.medium)
            .background(if (selected) FloconTheme.colors.ink.copy(alpha = SelectedAlpha) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun SidebarTitle(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FloconTheme.typography.listItem,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = FloconTheme.colors.ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun SidebarSectionHeader(
    title: String,
    count: Int,
) {
    DeeplinkSectionLabel(
        text = title,
        count = count,
        modifier = Modifier.padding(
            start = FloconTheme.spacing.small,
            top = FloconTheme.spacing.medium,
            bottom = FloconTheme.spacing.extraSmall,
        ),
    )
}

@Composable
@Preview
private fun DeeplinkSidebarPreview() {
    FloconTheme {
        DeeplinkSidebar(
            deepLinks = listOf(
                previewDeeplinkViewState(),
                previewDeeplinkViewState().copy(
                    deeplinkId = 1L,
                    label = "Open a profile",
                    parts = listOf(
                        DeeplinkPart.Text("myapp://profile/"),
                        DeeplinkPart.Variable(value = "{userId}", name = "userId", isResolved = false),
                    ),
                ),
                previewDeeplinkViewState().copy(deeplinkId = 2L, label = null, isHistory = true),
            ),
            variablesCount = 2,
            selectedTabId = DeeplinkTabUiModel.Freeform.id,
            filter = "",
            onAction = {},
            modifier = Modifier
                .background(FloconTheme.colors.islandSidebar)
                .fillMaxHeight(),
        )
    }
}
