package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_copy
import flocondesktop.composeapp.generated.resources.deeplink_detected_variables
import flocondesktop.composeapp.generated.resources.deeplink_fields_title
import flocondesktop.composeapp.generated.resources.deeplink_freeform_description
import flocondesktop.composeapp.generated.resources.deeplink_no_fields
import flocondesktop.composeapp.generated.resources.deeplink_no_variables
import flocondesktop.composeapp.generated.resources.deeplink_recent_links
import flocondesktop.composeapp.generated.resources.deeplink_resolved_preview
import flocondesktop.composeapp.generated.resources.deeplink_reuse
import flocondesktop.composeapp.generated.resources.deeplink_send
import flocondesktop.composeapp.generated.resources.deeplink_shared_variables
import flocondesktop.composeapp.generated.resources.deeplink_tab_freeform
import flocondesktop.composeapp.generated.resources.deeplink_url
import flocondesktop.composeapp.generated.resources.deeplink_used_by
import flocondesktop.composeapp.generated.resources.deeplink_variable_missing
import flocondesktop.composeapp.generated.resources.deeplink_variable_resolved
import flocondesktop.composeapp.generated.resources.freeform_link
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.buildLink
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.parseFreeformLink
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource

private val VariableChipWidth = 240.dp

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
                variables = state.variables,
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
    val fields = remember(item) { item.parts.filterIsInstance<DeeplinkPart.TextField>() }
    val values = remember(item.deeplinkId, item.isHistory) { mutableStateMapOf<DeeplinkPart.TextField, String>() }
    val link = item.parts.buildLink(values)
    val sendEnabled = canSend &&
        item.missingVariablesCount == 0 &&
        fields.all { !values[it].isNullOrBlank() }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(FloconTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        if (!item.isHistory) {
            DeeplinkDetailHeader(title = item.displayName, description = item.description)
        }
        DeeplinkUrlCard(
            onCopy = { onAction(DeeplinkScreenAction.CopyLink(link)) },
            onSend = { onAction(DeeplinkScreenAction.Submit(item, values.toMap())) },
            sendEnabled = sendEnabled,
            onDelete = if (item.isHistory) {
                { onAction(DeeplinkScreenAction.RemoveFromHistory(item)) }
            } else {
                null
            },
        ) {
            DeeplinkLinkText(parts = item.parts, fieldValues = values)
        }
        if (!item.isHistory) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
                modifier = Modifier.fillMaxWidth(),
            ) {
                DeeplinkDetailColumn(
                    title = stringResource(Res.string.deeplink_shared_variables),
                    modifier = Modifier.weight(1f),
                ) {
                    if (variables.isEmpty()) {
                        EmptyHint(stringResource(Res.string.deeplink_no_variables))
                    }
                    variables.forEach { variable ->
                        DeeplinkVariableEditor(variable = variable, onAction = onAction)
                    }
                }
                DeeplinkDetailColumn(
                    title = stringResource(Res.string.deeplink_fields_title),
                    modifier = Modifier.weight(1f),
                ) {
                    if (fields.isEmpty()) {
                        EmptyHint(stringResource(Res.string.deeplink_no_fields))
                    }
                    fields.forEach { field ->
                        Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)) {
                            Text(
                                text = field.label,
                                style = FloconTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = FloconTheme.colors.ink,
                            )
                            DeeplinkFieldInput(
                                part = field,
                                value = values[field].orEmpty(),
                                onValueChange = { values[field] = it },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeeplinkFreeformDetail(
    freeformLink: String,
    variables: List<DeeplinkVariableViewState>,
    recent: List<DeeplinkViewState>,
    canSend: Boolean,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parts = remember(freeformLink, variables) { parseFreeformLink(freeformLink.trim(), variables) }
    val link = parts.buildLink()
    val detected = remember(parts, variables) {
        val names = parts.filterIsInstance<DeeplinkPart.Variable>().map { it.name }.toSet()
        variables.filter { it.name in names }
    }
    val canSubmit = canSend &&
        link.isNotBlank() &&
        parts.none { it is DeeplinkPart.Variable && !it.isResolved }

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
        DeeplinkDetailColumn(title = stringResource(Res.string.deeplink_url)) {
            DeeplinkUrlCard(
                onCopy = { onAction(DeeplinkScreenAction.CopyLink(link)) },
                onSend = { onAction(DeeplinkScreenAction.SubmitFreeform) },
                sendEnabled = canSubmit,
            ) {
                FloconTextFieldWithoutM3(
                    value = freeformLink,
                    onValueChange = { onAction(DeeplinkScreenAction.SetFreeformLink(it)) },
                    placeholder = defaultPlaceHolder(
                        stringResource(Res.string.freeform_link),
                        color = FloconTheme.colors.inkMuted,
                    ),
                    textStyle = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    containerColor = FloconTheme.colors.bg200,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent {
                            val isEnter = it.key == Key.Enter && it.type == KeyEventType.KeyDown
                            if (isEnter && canSubmit) {
                                onAction(DeeplinkScreenAction.SubmitFreeform)
                            }
                            isEnter
                        },
                )
            }
        }
        if (detected.isNotEmpty()) {
            DeeplinkDetailColumn(title = stringResource(Res.string.deeplink_resolved_preview)) {
                DeeplinkLinkText(
                    parts = parts,
                    fieldValues = emptyMap(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colors.bg100)
                        .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)
                        .padding(FloconTheme.spacing.medium),
                )
            }
            DeeplinkDetailColumn(title = stringResource(Res.string.deeplink_detected_variables)) {
                detected.forEach { variable ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                    ) {
                        DeeplinkVariableEditor(variable = variable, onAction = onAction)
                        val isResolved = variable.value.isNotEmpty()
                        Text(
                            text = stringResource(
                                if (isResolved) {
                                    Res.string.deeplink_variable_resolved
                                } else {
                                    Res.string.deeplink_variable_missing
                                }
                            ),
                            style = FloconTheme.typography.bodySmall,
                            color = if (isResolved) FloconTheme.colors.statusSuccess else FloconTheme.colors.statusWarning,
                        )
                    }
                }
            }
        }
        if (recent.isNotEmpty()) {
            DeeplinkDetailColumn(title = stringResource(Res.string.deeplink_recent_links)) {
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

/** The link on a card, with its actions: copy, send and, for a link of the history, delete. */
@Composable
private fun DeeplinkUrlCard(
    onCopy: () -> Unit,
    onSend: () -> Unit,
    sendEnabled: Boolean,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .fillMaxWidth()
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg100)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.small),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = FloconTheme.spacing.small),
        ) {
            content()
        }
        FloconOutlinedButton(onClick = onCopy) {
            Text(stringResource(Res.string.deeplink_copy))
        }
        onDelete?.let {
            FloconIconTonalButton(
                onClick = it,
                containerColor = FloconTheme.colors.ink,
            ) {
                FloconIcon(imageVector = Icons.Default.Delete)
            }
        }
        FloconButton(
            onClick = { if (sendEnabled) onSend() },
            containerColor = FloconTheme.colors.accent,
            modifier = Modifier.alpha(if (sendEnabled) 1f else 0.45f),
        ) {
            Text(
                text = stringResource(Res.string.deeplink_send),
                color = FloconTheme.colors.accentInk,
                fontWeight = FontWeight.Bold,
            )
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
                modifier = Modifier.width(VariableChipWidth),
            )
            Text(
                text = stringResource(Res.string.deeplink_used_by, variable.usedByCount),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
                maxLines = 1,
                softWrap = false,
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
private fun DeeplinkDetailColumn(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = title,
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.inkMuted,
        )
        content()
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.inkFaint,
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
                            DeeplinkPart.Text("?tab="),
                            DeeplinkPart.TextField("tab", autoComplete = listOf("posts", "friends")),
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
                .background(FloconTheme.colors.bg000)
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
                variables = listOf(
                    DeeplinkVariableViewState(name = "userId", description = null, value = ""),
                    DeeplinkVariableViewState(name = "env", description = null, value = "staging"),
                ),
            ),
            freeformLink = "myapp://profile/{userId}?env={env}",
            onAction = {},
            modifier = Modifier
                .background(FloconTheme.colors.bg000)
                .fillMaxSize(),
        )
    }
}
