@file:OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preference_cancel
import flocondesktop.composeapp.generated.resources.preference_copy
import flocondesktop.composeapp.generated.resources.preference_decrement
import flocondesktop.composeapp.generated.resources.preference_edit
import flocondesktop.composeapp.generated.resources.preference_increment
import flocondesktop.composeapp.generated.resources.preference_read_only
import flocondesktop.composeapp.generated.resources.preference_save
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceValueTypeUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.asString
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesBooleanRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesFloatRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesIntRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesJsonRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesLongRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesStringRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesStringSetRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip
import org.jetbrains.compose.resources.stringResource

private val ActionSize = 30.dp
private val FieldRadius = 7.dp
private val IntFieldWidth = 160.dp
private val MonoSmall = 12.sp

@Composable
fun SharedPreferenceRow(
    model: SharedPreferencesRowUiModel,
    isExpanded: Boolean,
    onToggleExpanded: (key: String) -> Unit,
    onValueChanged: (row: SharedPreferencesRowUiModel, valueString: String) -> Unit,
    onEditClicked: (row: SharedPreferencesRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val value = model.value
    val collapsible = model.collapsible
    val isOpen = collapsible != null && (collapsible.alwaysOpen || isExpanded)
    val originalText = remember(value) { value.asString() }
    var text by remember(value) { mutableStateOf(originalText) }
    val isEditableText = collapsible == null && (
        value is SharedPreferencesRowUiModel.Value.StringValue ||
            value is SharedPreferencesRowUiModel.Value.IntValue ||
            value is SharedPreferencesRowUiModel.Value.LongValue ||
            value is SharedPreferencesRowUiModel.Value.FloatValue
        )
    val isDirty = isEditableText && text != originalText
    val lineColor = colors.line

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isDirty) colors.accent.copy(alpha = 0.05f) else Color.Transparent)
            .drawBehind {
                val y = size.height - 0.5f
                drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            },
    ) {
        PreferenceGridRow(
            modifier = Modifier
                .heightIn(min = FloconTheme.sizes.rowHeight + FloconTheme.spacing.compact * 2)
                .then(
                    if (collapsible != null && !collapsible.alwaysOpen) {
                        Modifier
                            .clickable { onToggleExpanded(model.key) }
                            .pointerHoverIcon(PointerIcon.Hand)
                    } else {
                        Modifier
                    },
                )
                .padding(vertical = FloconTheme.spacing.compact),
            key = {
                Text(
                    text = model.key,
                    style = FloconTheme.typography.monoMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.ink,
                    modifier = Modifier.weight(KeyColumnWeight),
                )
            },
            type = {
                Box(Modifier.width(FloconTheme.sizes.columnSmall)) {
                    TypeBadge(model.type)
                }
            },
            value = {
                Box(Modifier.weight(ValueColumnWeight), contentAlignment = Alignment.CenterStart) {
                    when {
                        value is SharedPreferencesRowUiModel.Value.BooleanValue -> BooleanValue(
                            checked = value.value,
                            onCheckedChange = { onValueChanged(model, it.toString()) },
                        )

                        value is SharedPreferencesRowUiModel.Value.StringSetValue -> FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                        ) {
                            value.value.forEach { Chip(it) }
                        }

                        collapsible != null -> CollapsedValue(collapsible, isOpen, showChevron = !collapsible.alwaysOpen)

                        else -> PreferenceValueField(
                            value = text,
                            onValueChange = { text = it },
                            isDirty = isDirty,
                            onSave = { onValueChanged(model, text) },
                            onCancel = { text = originalText },
                            onStep = if (value is SharedPreferencesRowUiModel.Value.IntValue || value is SharedPreferencesRowUiModel.Value.LongValue) {
                                { delta -> text.trim().toLongOrNull()?.let { text = (it + delta).toString() } }
                            } else {
                                null
                            },
                            modifier = if (value is SharedPreferencesRowUiModel.Value.StringValue) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.width(IntFieldWidth)
                            },
                        )
                    }
                }
            },
            actions = {
                Row(
                    modifier = Modifier.width(FloconTheme.sizes.columnSmall),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RowActions(
                        model = model,
                        isDirty = isDirty,
                        isExpanded = isOpen,
                        onCancel = { text = originalText },
                        onSave = { onValueChanged(model, text) },
                        onEditClicked = onEditClicked,
                    )
                }
            },
        )

        if (collapsible != null && isOpen) {
            PreferenceGridRow(
                modifier = Modifier.padding(bottom = FloconTheme.spacing.medium),
                key = { Box(Modifier.weight(KeyColumnWeight)) },
                type = { Box(Modifier.width(FloconTheme.sizes.columnSmall)) },
                value = {
                    ExpandedCode(
                        collapsible = collapsible,
                        modifier = Modifier.weight(ValueColumnWeight),
                    )
                },
                actions = { Box(Modifier.width(FloconTheme.sizes.columnSmall)) },
            )
        }
    }
}

@Composable
private fun RowScope.RowActions(
    model: SharedPreferencesRowUiModel,
    isDirty: Boolean,
    isExpanded: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onEditClicked: (row: SharedPreferencesRowUiModel) -> Unit,
) {
    val colors = FloconTheme.colors
    val value = model.value
    val clipboard = LocalClipboardManager.current

    when {
        isDirty -> {
            ActionButton(
                icon = Icons.Outlined.Close,
                tooltip = stringResource(Res.string.preference_cancel),
                onClick = onCancel,
                containerColor = colors.bg300,
                contentColor = colors.ink,
                borderColor = colors.lineStrong,
            )
            ActionButton(
                icon = Icons.Outlined.Check,
                tooltip = stringResource(Res.string.preference_save),
                onClick = onSave,
                containerColor = colors.accent,
                contentColor = colors.accentInk,
                borderColor = colors.accent,
            )
        }

        value is SharedPreferencesRowUiModel.Value.StringSetValue -> Text(
            text = stringResource(Res.string.preference_read_only),
            style = FloconTheme.typography.labelSmall,
            color = colors.inkMuted,
            textAlign = TextAlign.End,
            maxLines = 1,
        )

        value is SharedPreferencesRowUiModel.Value.StringValue && model.collapsible != null -> {
            if (isExpanded) {
                ActionButton(
                    icon = Icons.Outlined.ContentCopy,
                    tooltip = stringResource(Res.string.preference_copy),
                    onClick = { clipboard.setText(AnnotatedString(value.value)) },
                    containerColor = colors.bg300,
                    contentColor = colors.ink,
                    borderColor = colors.lineStrong,
                )
            }
            ActionButton(
                icon = Icons.Outlined.Edit,
                tooltip = stringResource(Res.string.preference_edit),
                onClick = { onEditClicked(model) },
                containerColor = colors.bg300,
                contentColor = colors.ink,
                borderColor = colors.lineStrong,
            )
        }

        else -> Unit
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    tooltip: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
) {
    val shape = RoundedCornerShape(FieldRadius)
    WithTooltip(tooltip) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(ActionSize)
                .background(containerColor, shape)
                .border(1.dp, borderColor, shape)
                .clickable(onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            FloconIcon(icon, tint = contentColor, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun PreferenceSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = FloconTheme.colors
    val thumbOffset by animateDpAsState(if (checked) 18.dp else 2.dp)
    val trackColor by animateColorAsState(if (checked) colors.accent else colors.bg300)
    val thumbColor by animateColorAsState(if (checked) colors.accentInk else colors.inkMuted)
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .size(width = 36.dp, height = 20.dp)
            .background(trackColor, shape)
            .border(1.dp, if (checked) colors.accent else colors.lineStrong, shape)
            .clip(shape)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(16.dp)
                .background(thumbColor, CircleShape),
        )
    }
}

@Composable
private fun BooleanValue(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    var local by remember(checked) { mutableStateOf(checked) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        PreferenceSwitch(
            checked = local,
            onCheckedChange = {
                local = it
                onCheckedChange(it)
            },
        )
        Text(
            text = local.toString(),
            style = FloconTheme.typography.monoSmall,
            color = FloconTheme.colors.inkMuted,
        )
    }
}

@Composable
private fun Chip(text: String) {
    val colors = FloconTheme.colors
    Text(
        text = text,
        style = FloconTheme.typography.monoSmall.copy(fontSize = 11.sp),
        color = colors.ink,
        modifier = Modifier
            .background(colors.bg300, RoundedCornerShape(50))
            .border(1.dp, colors.lineStrong, RoundedCornerShape(50))
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
    )
}

@Composable
private fun CollapsedValue(
    collapsible: SharedPreferencesRowUiModel.Collapsible,
    isExpanded: Boolean,
    showChevron: Boolean,
) {
    val colors = FloconTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (showChevron) {
            FloconIcon(
                imageVector = if (isExpanded) Icons.Outlined.ExpandMore else Icons.Filled.ChevronRight,
                tint = colors.inkMuted,
                modifier = Modifier.size(16.dp),
            )
        }
        if (!isExpanded) {
            Text(
                text = collapsible.preview,
                style = FloconTheme.typography.monoSmall,
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        Text(
            text = collapsible.sizeLabel,
            style = FloconTheme.typography.labelSmall,
            color = colors.inkMuted,
            maxLines = 1,
            modifier = Modifier
                .background(colors.bg300, RoundedCornerShape(50))
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        )
    }
}

@Composable
private fun ExpandedCode(
    collapsible: SharedPreferencesRowUiModel.Collapsible,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val code = remember(collapsible.expandedText, collapsible.isJson, colors) {
        if (collapsible.isJson) {
            highlightJson(
                collapsible.expandedText,
                JsonHighlightColors(
                    key = colors.catNetwork,
                    string = colors.catDatabase,
                    number = colors.catAccessibility,
                    punctuation = colors.ink,
                ),
            )
        } else {
            AnnotatedString(collapsible.expandedText)
        }
    }
    Box(
        modifier = modifier
            .background(colors.bg000, RoundedCornerShape(8.dp))
            .padding(FloconTheme.spacing.medium),
    ) {
        SelectionContainer {
            Text(
                text = code,
                color = colors.ink,
                style = FloconTheme.typography.monoSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = MonoSmall,
                    lineHeight = 18.sp,
                ),
            )
        }
    }
}

@Composable
private fun PreferenceValueField(
    value: String,
    onValueChange: (String) -> Unit,
    isDirty: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    // +1 / -1 for the numeric fields
    onStep: ((delta: Int) -> Unit)? = null,
) {
    val colors = FloconTheme.colors
    val shape = RoundedCornerShape(FieldRadius)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = FloconTheme.typography.monoMedium.copy(color = colors.ink),
        cursorBrush = SolidColor(colors.accent),
        modifier = modifier
            .height(ActionSize)
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) {
                    false
                } else {
                    when (it.key) {
                        Key.Enter, Key.NumPadEnter -> {
                            if (isDirty) onSave()
                            true
                        }

                        Key.Escape -> {
                            if (isDirty) onCancel()
                            isDirty
                        }

                        Key.DirectionUp -> {
                            onStep?.invoke(1)
                            onStep != null
                        }

                        Key.DirectionDown -> {
                            onStep?.invoke(-1)
                            onStep != null
                        }

                        else -> false
                    }
                }
            },
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ActionSize)
                    .background(colors.bg000, shape)
                    .border(1.dp, if (isDirty) colors.accent else colors.line, shape)
                    .padding(start = FloconTheme.spacing.small),
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) { inner() }
                if (onStep != null) {
                    Column(Modifier.width(18.dp)) {
                        StepButton(Icons.Filled.KeyboardArrowUp, stringResource(Res.string.preference_increment)) { onStep(1) }
                        StepButton(Icons.Filled.KeyboardArrowDown, stringResource(Res.string.preference_decrement)) { onStep(-1) }
                    }
                }
            }
        },
    )
}

@Composable
private fun ColumnScope.StepButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .hoverable(interaction)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (hovered) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
            modifier = Modifier.size(10.dp),
        )
    }
}

@Composable
private fun TypeBadge(
    type: PreferenceValueTypeUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (strong, soft) = when (type) {
        PreferenceValueTypeUiModel.Str -> colors.catDatabase to colors.catDatabaseSoft

        PreferenceValueTypeUiModel.Int,
        PreferenceValueTypeUiModel.Long,
        PreferenceValueTypeUiModel.Float,
        -> colors.catNetwork to colors.catNetworkSoft

        PreferenceValueTypeUiModel.Bool -> colors.accent to colors.accentSoft

        PreferenceValueTypeUiModel.Json -> colors.catDeeplink to colors.catDeeplinkSoft

        PreferenceValueTypeUiModel.Set -> colors.catAccessibility to colors.catAccessibilitySoft
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(20.dp)
            .background(soft, RoundedCornerShape(5.dp))
            .padding(horizontal = 7.dp),
    ) {
        Text(
            text = type.label,
            style = FloconTheme.typography.monoSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.06.em,
            ),
            color = strong,
            maxLines = 1,
        )
    }
}

@Composable
private fun PreviewRow(model: SharedPreferencesRowUiModel, isExpanded: Boolean = false) {
    FloconTheme {
        Box(Modifier.background(FloconTheme.colors.bg100)) {
            SharedPreferenceRow(
                model = model,
                isExpanded = isExpanded,
                onToggleExpanded = {},
                onValueChanged = { _, _ -> },
                onEditClicked = {},
            )
        }
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview() = PreviewRow(previewSharedPreferencesStringRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowIntPreview() = PreviewRow(previewSharedPreferencesIntRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowFloatPreview() = PreviewRow(previewSharedPreferencesFloatRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowBooleanPreview() = PreviewRow(previewSharedPreferencesBooleanRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowLongPreview() = PreviewRow(previewSharedPreferencesLongRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowSetPreview() = PreviewRow(previewSharedPreferencesStringSetRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowJsonPreview() = PreviewRow(previewSharedPreferencesJsonRowUiModel())

@Preview
@Composable
private fun SharedPreferenceRowJsonExpandedPreview() = PreviewRow(previewSharedPreferencesJsonRowUiModel(), isExpanded = true)
