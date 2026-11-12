package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import io.github.openflocon.library.designsystem.components.FloconActionButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconTextButton
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import org.jetbrains.compose.resources.stringResource

private val DetectedVariableWidth = 380.dp
private val VariablesListMaxWidth = 640.dp
private val ActionIconSize = 16.dp

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

    DeeplinkDetailPage(modifier = modifier) {
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
                DeeplinkDetailSection(
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
                DeeplinkDetailSection(
                    title = stringResource(Res.string.deeplink_fields_title),
                    modifier = Modifier.weight(1f),
                ) {
                    if (fields.isEmpty()) {
                        EmptyHint(stringResource(Res.string.deeplink_no_fields))
                    }
                    fields.forEach { field ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                        ) {
                            Text(
                                text = field.label,
                                style = FloconTheme.typography.listItem,
                                fontWeight = FontWeight.SemiBold,
                                color = FloconTheme.colors.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(DeeplinkInputNameWidth),
                            )
                            DeeplinkFieldInput(
                                part = field,
                                value = values[field].orEmpty(),
                                onValueChange = { values[field] = it },
                                modifier = Modifier.weight(1f),
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

    DeeplinkDetailPage(modifier = modifier) {
        DeeplinkDetailHeader(
            title = stringResource(Res.string.deeplink_tab_freeform),
            description = stringResource(Res.string.deeplink_freeform_description),
        )
        DeeplinkDetailSection(title = stringResource(Res.string.deeplink_url)) {
            DeeplinkUrlCard(
                onCopy = { onAction(DeeplinkScreenAction.CopyLink(link)) },
                onSend = { onAction(DeeplinkScreenAction.SubmitFreeform) },
                sendEnabled = canSubmit,
            ) {
                FloconTextFieldWithoutM3(
                    value = freeformLink,
                    onValueChange = { onAction(DeeplinkScreenAction.SetFreeformLink(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.freeform_link),
                            style = deeplinkLinkTextStyle(),
                            color = FloconTheme.colors.inkFaint,
                        )
                    },
                    textStyle = deeplinkLinkTextStyle(),
                    containerColor = Color.Transparent,
                    contentColor = FloconTheme.colors.ink,
                    visualTransformation = rememberFreeformLinkHighlight(variables),
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
            DeeplinkDetailSection(title = stringResource(Res.string.deeplink_resolved_preview)) {
                DeeplinkLinkText(
                    parts = parts,
                    fieldValues = emptyMap(),
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = FloconTheme.colors.inkMuted,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .deeplinkCard()
                        .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                )
            }
            DeeplinkDetailSection(title = stringResource(Res.string.deeplink_detected_variables)) {
                detected.forEach { variable ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                    ) {
                        DeeplinkVariableChip(
                            variable = variable,
                            onValueChange = { onAction(DeeplinkScreenAction.SetVariable(variable.name, it)) },
                            modifier = Modifier.width(DetectedVariableWidth),
                        )
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
            DeeplinkDetailSection(
                title = stringResource(Res.string.deeplink_recent_links),
                spacing = FloconTheme.spacing.none,
            ) {
                recent.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = FloconTheme.sizes.rowHeight)
                            .bottomLine(FloconTheme.colors.line),
                    ) {
                        Text(
                            text = item.displayName,
                            style = FloconTheme.typography.listItemMono,
                            color = FloconTheme.colors.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        FloconTextButton(
                            onClick = { onAction(DeeplinkScreenAction.SetFreeformLink(item.displayName)) },
                            containerColor = Color.Transparent,
                        ) {
                            Text(
                                text = stringResource(Res.string.deeplink_reuse),
                                style = FloconTheme.typography.bodySmall,
                                color = FloconTheme.colors.inkMuted,
                            )
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
    DeeplinkDetailPage(modifier = modifier) {
        DeeplinkDetailHeader(
            title = stringResource(Res.string.deeplink_shared_variables),
            description = null,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            modifier = Modifier.widthIn(max = VariablesListMaxWidth),
        ) {
            variables.forEach { variable ->
                DeeplinkVariableEditor(variable = variable, onAction = onAction)
            }
        }
    }
}

/** The scrolling page of a tab, with the rhythm of the mockup. */
@Composable
private fun DeeplinkDetailPage(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = FloconTheme.spacing.extraLarge, vertical = FloconTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
        content = content,
    )
}

@Composable
private fun Modifier.deeplinkCard(): Modifier = this
    .clip(FloconTheme.shapes.medium)
    .background(FloconTheme.colors.bg200)
    .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)

private fun Modifier.bottomLine(color: Color): Modifier = drawBehind {
    val y = size.height - 0.5.dp.toPx()
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
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
            .deeplinkCard()
            .padding(start = FloconTheme.spacing.large, end = FloconTheme.spacing.medium)
            .padding(vertical = FloconTheme.spacing.medium),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = FloconTheme.spacing.small),
        ) {
            content()
        }
        FloconActionButton(onClick = onCopy) {
            Text(stringResource(Res.string.deeplink_copy))
        }
        onDelete?.let {
            FloconActionButton(onClick = it) {
                FloconIcon(imageVector = Icons.Outlined.Delete, modifier = Modifier.size(ActionIconSize))
            }
        }
        FloconActionButton(
            onClick = onSend,
            primary = true,
            enabled = sendEnabled,
            modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall),
        ) {
            Text(
                text = stringResource(Res.string.deeplink_send),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
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
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        DeeplinkVariableChip(
            variable = variable,
            onValueChange = { onAction(DeeplinkScreenAction.SetVariable(variable.name, it)) },
            modifier = Modifier.fillMaxWidth(),
        )
        variable.description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkFaint,
                modifier = Modifier.padding(start = DeeplinkInputNameWidth + FloconTheme.spacing.medium),
            )
        }
    }
}

@Composable
private fun DeeplinkDetailHeader(
    title: String,
    description: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)) {
        Text(
            text = title,
            style = FloconTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
        )
        description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.inkMuted,
            )
        }
    }
}

@Composable
private fun DeeplinkDetailSection(
    title: String,
    modifier: Modifier = Modifier,
    spacing: Dp = FloconTheme.spacing.small,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        DeeplinkSectionLabel(
            text = title,
            modifier = Modifier.padding(bottom = FloconTheme.spacing.extraSmall),
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
