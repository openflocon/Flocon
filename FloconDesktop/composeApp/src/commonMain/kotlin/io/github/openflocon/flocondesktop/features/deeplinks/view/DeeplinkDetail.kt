package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_freeform_description
import flocondesktop.composeapp.generated.resources.deeplink_recent_links
import flocondesktop.composeapp.generated.resources.deeplink_reuse
import flocondesktop.composeapp.generated.resources.deeplink_shared_variables
import flocondesktop.composeapp.generated.resources.deeplink_tab_freeform
import flocondesktop.composeapp.generated.resources.deeplink_used_by
import flocondesktop.composeapp.generated.resources.freeform_link
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource

/** The content of the selected tab. */
@Composable
internal fun DeeplinkDetail(
    state: DeeplinkScreenState,
    freeformLink: String,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val tab = state.selectedTab) {
        DeeplinkTabUiModel.Freeform -> {
            val recent = remember(state.deepLinks) { state.deepLinks.filter { it.isHistory } }
            DeeplinkFreeformDetail(
                freeformLink = freeformLink,
                recent = recent,
                canSend = state.canSend,
                onAction = onAction,
                modifier = modifier,
            )
        }

        DeeplinkTabUiModel.Variables -> DeeplinkVariablesDetail(
            variables = state.variables,
            onAction = onAction,
            modifier = modifier,
        )

        is DeeplinkTabUiModel.Link -> {
            val item = remember(state.deepLinks, tab) {
                state.deepLinks.firstOrNull { it.deeplinkId == tab.deeplinkId && it.isHistory == tab.isHistory }
            }
            if (item != null) {
                val variables = remember(state.variables, item) {
                    state.variables.filter { it.name in item.variableNames }
                }
                DeeplinkLinkDetail(
                    item = item,
                    variables = variables,
                    canSend = state.canSend,
                    onAction = onAction,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
private fun DeeplinkLinkDetail(
    item: DeeplinkViewState,
    variables: List<DeeplinkVariableViewState>,
    canSend: Boolean,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // the title and the description are shown above: the row only keeps the link
    val row = remember(item) { item.copy(label = null, description = null) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(FloconTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        if (!item.isHistory) {
            DeeplinkDetailHeader(title = item.displayName, description = item.description)
        }
        DeeplinkRow(
            item = row,
            submit = { link, values -> onAction(DeeplinkScreenAction.Submit(link, values)) },
            removeFromHistory = { onAction(DeeplinkScreenAction.RemoveFromHistory(it)) },
            enabled = canSend && item.missingVariablesCount == 0,
            modifier = Modifier.fillMaxWidth(),
        )
        if (variables.isNotEmpty()) {
            SectionTitle(stringResource(Res.string.deeplink_shared_variables))
            variables.forEach { variable ->
                DeeplinkVariableEditor(variable = variable, onAction = onAction)
            }
        }
    }
}

@Composable
private fun DeeplinkFreeformDetail(
    freeformLink: String,
    recent: List<DeeplinkViewState>,
    canSend: Boolean,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canSubmit = canSend && freeformLink.isNotBlank()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(FloconTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        DeeplinkDetailHeader(
            title = stringResource(Res.string.deeplink_tab_freeform),
            description = stringResource(Res.string.deeplink_freeform_description),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconTextFieldWithoutM3(
                value = freeformLink,
                onValueChange = { onAction(DeeplinkScreenAction.SetFreeformLink(it)) },
                placeholder = defaultPlaceHolder(
                    stringResource(Res.string.freeform_link),
                    color = FloconTheme.colors.inkMuted,
                ),
                textStyle = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                containerColor = FloconTheme.colors.bg200,
                modifier = Modifier
                    .weight(1f)
                    .onPreviewKeyEvent {
                        val isEnter = it.key == Key.Enter && it.type == KeyEventType.KeyDown
                        if (isEnter && canSubmit) {
                            onAction(DeeplinkScreenAction.SubmitFreeform)
                        }
                        isEnter
                    },
            )
            FloconIconTonalButton(
                onClick = { onAction(DeeplinkScreenAction.SubmitFreeform) },
                enabled = canSubmit,
                containerColor = FloconTheme.colors.ink,
            ) {
                FloconIcon(imageVector = Icons.AutoMirrored.Filled.Send)
            }
        }
        if (recent.isNotEmpty()) {
            SectionTitle(stringResource(Res.string.deeplink_recent_links))
            recent.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                ) {
                    Text(
                        text = item.displayName,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    FloconOutlinedButton(
                        onClick = { onAction(DeeplinkScreenAction.SetFreeformLink(item.displayName)) },
                    ) {
                        Text(stringResource(Res.string.deeplink_reuse))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeeplinkVariablesDetail(
    variables: List<DeeplinkVariableViewState>,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(FloconTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        DeeplinkDetailHeader(
            title = stringResource(Res.string.deeplink_shared_variables),
            description = null,
        )
        variables.forEach { variable ->
            DeeplinkVariableEditor(variable = variable, onAction = onAction)
        }
    }
}

@Composable
private fun DeeplinkVariableEditor(
    variable: DeeplinkVariableViewState,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        ) {
            DeeplinkVariableChip(
                variable = variable,
                onValueChange = { onAction(DeeplinkScreenAction.SetVariable(variable.name, it)) },
            )
            Text(
                text = stringResource(Res.string.deeplink_used_by, variable.usedByCount),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
            )
        }
        variable.description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
            )
        }
    }
}

@Composable
private fun DeeplinkDetailHeader(
    title: String,
    description: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)) {
        Text(
            text = title,
            style = FloconTheme.typography.titleSmall,
            color = FloconTheme.colors.ink,
        )
        description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = FloconTheme.colors.inkMuted,
    )
}

@Composable
@Preview
private fun DeeplinkLinkDetailPreview() {
    FloconTheme {
        DeeplinkDetail(
            state = DeeplinkScreenState(
                deepLinks = listOf(
                    previewDeeplinkViewState().copy(
                        parts = listOf(
                            DeeplinkPart.Text("myapp://profile/"),
                            DeeplinkPart.Variable(value = "{userId}", name = "userId", isResolved = false),
                        ),
                    ),
                ),
                variables = listOf(
                    DeeplinkVariableViewState(
                        name = "userId",
                        description = "The user id",
                        value = "",
                        usedByCount = 3,
                    ),
                ),
                selectedTab = DeeplinkTabUiModel.Link(deeplinkId = 0L, isHistory = false, title = "label"),
            ),
            freeformLink = "",
            onAction = {},
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .fillMaxSize(),
        )
    }
}

@Composable
@Preview
private fun DeeplinkFreeformDetailPreview() {
    FloconTheme {
        DeeplinkDetail(
            state = DeeplinkScreenState(
                deepLinks = listOf(previewDeeplinkViewState().copy(isHistory = true, label = "myapp://settings")),
                variables = emptyList(),
            ),
            freeformLink = "myapp://home",
            onAction = {},
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .fillMaxSize(),
        )
    }
}
