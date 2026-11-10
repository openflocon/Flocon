package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preference_cancel
import flocondesktop.composeapp.generated.resources.preference_copy
import flocondesktop.composeapp.generated.resources.preference_send_to_app
import flocondesktop.composeapp.generated.resources.preference_status_invalid
import flocondesktop.composeapp.generated.resources.preference_status_modified
import flocondesktop.composeapp.generated.resources.preference_status_modified_valid
import flocondesktop.composeapp.generated.resources.preference_tab_formatted
import flocondesktop.composeapp.generated.resources.preference_tab_raw
import flocondesktop.composeapp.generated.resources.preference_type_json
import flocondesktop.composeapp.generated.resources.preference_type_text
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferenceEditViewModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditStatus
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditTab
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceEditUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewPreferenceEditUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val EditorTextSize = 12.sp
private val EditorLineHeight = 19.sp
private val EditorMinWidth = 360.dp

@Composable
fun SharedPreferenceEditPanel(
    key: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<SharedPreferenceEditViewModel>(key = key) { parametersOf(key) }
    val state by viewModel.uiState

    SharedPreferenceEditPanel(
        state = state,
        onTextChanged = viewModel::onTextChanged,
        onTabSelected = viewModel::onTabSelected,
        onCancel = viewModel::onCancel,
        onCopy = viewModel::onCopy,
        onSend = viewModel::onSend,
        modifier = modifier,
    )
}

@Composable
fun SharedPreferenceEditPanel(
    state: PreferenceEditUiModel,
    onTextChanged: (String) -> Unit,
    onTabSelected: (PreferenceEditTab) -> Unit,
    onCancel: () -> Unit,
    onCopy: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .fillMaxSize()
            .padding(FloconTheme.spacing.large),
    ) {
        EditHeader(state = state, onCopy = onCopy)
        if (state.isJson) {
            EditTabs(selected = state.tab, onTabSelected = onTabSelected)
        }
        CodeEditor(
            state = state,
            onTextChanged = onTextChanged,
            onSend = onSend,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
        EditFooter(state = state, onCancel = onCancel, onSend = onSend)
    }
}

@Composable
private fun EditHeader(
    state: PreferenceEditUiModel,
    onCopy: () -> Unit,
) {
    val type = stringResource(if (state.isJson) Res.string.preference_type_json else Res.string.preference_type_text)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)) {
            Text(
                text = state.key,
                style = FloconTheme.typography.monoMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = FloconTheme.colors.ink,
            )
            Text(
                text = "$type · ${state.sizeLabel}",
                style = FloconTheme.typography.labelMedium,
                color = FloconTheme.colors.inkMuted,
            )
        }
        FloconIconButton(
            imageVector = Icons.Outlined.ContentCopy,
            onClick = onCopy,
            tooltip = stringResource(Res.string.preference_copy),
        )
    }
}

@Composable
private fun EditTabs(
    selected: PreferenceEditTab,
    onTabSelected: (PreferenceEditTab) -> Unit,
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large)) {
            EditTab(stringResource(Res.string.preference_tab_formatted), selected == PreferenceEditTab.Formatted) {
                onTabSelected(PreferenceEditTab.Formatted)
            }
            EditTab(stringResource(Res.string.preference_tab_raw), selected == PreferenceEditTab.Raw) {
                onTabSelected(PreferenceEditTab.Raw)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
    }
}

@Composable
private fun EditTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = FloconTheme.colors
    Column(
        // the underline takes the width of the label, not the whole row
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Text(
            text = text,
            style = FloconTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) colors.ink else colors.inkMuted,
            modifier = Modifier.padding(vertical = FloconTheme.spacing.small),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (isSelected) colors.accent else Color.Transparent),
        )
    }
}

/** Colors the json while typing, the text is not changed so the offsets are identical. */
private class JsonHighlightTransformation(private val colors: JsonHighlightColors) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(highlightJson(text.text, colors), OffsetMapping.Identity)
}

@Composable
private fun CodeEditor(
    state: PreferenceEditUiModel,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = RoundedCornerShape(8.dp)
    val focusRequester = remember { FocusRequester() }
    val textStyle = FloconTheme.typography.monoSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = EditorTextSize,
        lineHeight = EditorLineHeight,
        color = colors.ink,
    )
    val transformation = remember(state.isJson, colors) {
        if (state.isJson) {
            JsonHighlightTransformation(
                JsonHighlightColors(
                    key = colors.catNetwork,
                    string = colors.catDatabase,
                    number = colors.catAccessibility,
                    punctuation = colors.ink,
                ),
            )
        } else {
            VisualTransformation.None
        }
    }
    val lineNumbers = remember(state.lineCount) { (1..state.lineCount).joinToString("\n") }
    val modified = state.status != PreferenceEditStatus.Unchanged
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Row(
        modifier = modifier
            .background(colors.bg000, shape)
            .border(1.dp, if (modified) colors.accent else colors.line, shape)
            .clip(shape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusRequester.requestFocus()
            }
            .verticalScroll(verticalScroll)
            .padding(vertical = FloconTheme.spacing.medium),
    ) {
        Text(
            text = lineNumbers,
            style = textStyle.copy(color = colors.inkFaint, textAlign = TextAlign.End),
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
        )
        Box(Modifier.width(1.dp).fillMaxHeight().background(colors.line))
        Box(
            Modifier
                .weight(1f)
                .horizontalScroll(horizontalScroll)
                .padding(start = FloconTheme.spacing.small),
        ) {
            BasicTextField(
                value = state.text,
                onValueChange = onTextChanged,
                textStyle = textStyle,
                cursorBrush = SolidColor(colors.accent),
                visualTransformation = transformation,
                modifier = Modifier
                    .widthIn(min = EditorMinWidth)
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyDown &&
                            (it.key == Key.Enter || it.key == Key.NumPadEnter) &&
                            (it.isMetaPressed || it.isCtrlPressed)
                        ) {
                            onSend()
                            true
                        } else {
                            false
                        }
                    },
            )
        }
    }
}

@Composable
private fun EditFooter(
    state: PreferenceEditUiModel,
    onCancel: () -> Unit,
    onSend: () -> Unit,
) {
    val colors = FloconTheme.colors
    val modified = state.status != PreferenceEditStatus.Unchanged
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = Modifier.fillMaxWidth(),
    ) {
        when (state.status) {
            PreferenceEditStatus.Unchanged -> Unit
            PreferenceEditStatus.Modified -> StatusText(stringResource(Res.string.preference_status_modified), colors.accent)
            PreferenceEditStatus.ModifiedValidJson -> StatusText(stringResource(Res.string.preference_status_modified_valid), colors.accent)
            PreferenceEditStatus.InvalidJson -> StatusText(stringResource(Res.string.preference_status_invalid), colors.statusError)
        }
        Spacer(Modifier.weight(1f))
        FooterButton(
            text = stringResource(Res.string.preference_cancel),
            onClick = onCancel,
            enabled = modified,
            containerColor = Color.Transparent,
            contentColor = colors.ink,
            border = BorderStroke(1.dp, colors.lineStrong),
        )
        FooterButton(
            text = stringResource(Res.string.preference_send_to_app),
            onClick = onSend,
            enabled = state.canSend,
            containerColor = colors.accent,
            contentColor = colors.accentInk,
            border = null,
        )
    }
}

@Composable
private fun StatusText(text: String, color: Color) {
    Text(text = text, style = FloconTheme.typography.labelMedium, color = color, maxLines = 1)
}

@Composable
private fun FooterButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    border: BorderStroke?,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .height(FloconTheme.sizes.controlHeight)
            .background(containerColor, shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.medium),
    ) {
        Text(
            text = text,
            style = FloconTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun SharedPreferenceEditPanelPreview() {
    FloconTheme {
        SharedPreferenceEditPanel(
            state = previewPreferenceEditUiModel(PreferenceEditStatus.ModifiedValidJson),
            onTextChanged = {},
            onTabSelected = {},
            onCancel = {},
            onCopy = {},
            onSend = {},
        )
    }
}

@Preview
@Composable
private fun SharedPreferenceEditPanelInvalidPreview() {
    FloconTheme {
        SharedPreferenceEditPanel(
            state = previewPreferenceEditUiModel(PreferenceEditStatus.InvalidJson),
            onTextChanged = {},
            onTabSelected = {},
            onCancel = {},
            onCopy = {},
            onSend = {},
        )
    }
}
