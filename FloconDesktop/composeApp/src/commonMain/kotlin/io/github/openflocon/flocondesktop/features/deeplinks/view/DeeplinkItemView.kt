package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeeplinkItemView(
    item: DeeplinkViewState,
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    removeFromHistory: (DeeplinkViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val values = remember(item.deeplinkId) { mutableStateMapOf<DeeplinkPart.TextField, String>() }

    Column(
        modifier = modifier
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item.label?.let {
            Text(
                text = item.label,
                modifier = Modifier.padding(start = 4.dp),
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = FloconTheme.colorPalette.onPrimary
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(FloconTheme.shapes.medium)
                    .background(
                        if (item.isHistory) FloconTheme.colorPalette.accent
                        else FloconTheme.colorPalette.surface
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item.parts.fastForEach { part ->
                    TextFieldPart(
                        part = part,
                        onFieldValueChanged = { field, value ->
                            values.put(field, value)
                        },
                    )
                }
            }

            if (item.isHistory) {
                FloconIconTonalButton(
                    onClick = { removeFromHistory(item) },
                    containerColor = FloconTheme.colorPalette.tertiary,
                ) {
                    FloconIcon(
                        imageVector = Icons.Default.Delete,
                    )
                }
            }

            FloconIconTonalButton(
                onClick = { submit(item, values.toMap()) },
                containerColor = FloconTheme.colorPalette.tertiary,
            ) {
                FloconIcon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                )
            }
        }
        item.description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                ),
                color = FloconTheme.colorPalette.onSurface,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextFieldPart(
    part: DeeplinkPart,
    onFieldValueChanged: (DeeplinkPart.TextField, value: String) -> Unit
) {
    when (part) {
        is DeeplinkPart.TextField -> {
            val onFieldValueChangedCallback by rememberUpdatedState(onFieldValueChanged)
            var value by remember { mutableStateOf("") }
            var isExpanded by remember { mutableStateOf(false) }

            LaunchedEffect(part, value) {
                onFieldValueChangedCallback(part, value)
            }

            val filteredAutoComplete = remember(value, part.autoComplete) {
                part.autoComplete?.filter { it.contains(value, ignoreCase = true) }
            }
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
            ) {
                DeeplinkTextField(
                    value = value,
                    onValueChange = {
                        value = it
                        isExpanded = filteredAutoComplete?.isNotEmpty() == true
                    },
                    modifier = Modifier.menuAnchor(PrimaryEditable),
                    label = part.label,
                )

                // The dropdown menu
                if (filteredAutoComplete?.isNotEmpty() == true) {
                    ExposedDropdownMenu(
                        modifier = Modifier.widthIn(min = 200.dp),
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                    ) {
                        filteredAutoComplete.forEach { item ->
                            Text(
                                text = item,
                                style = FloconTheme.typography.bodySmall,
                                modifier = Modifier
                                    .clickable {
                                        value = item
                                        isExpanded = false
                                    }
                                    .padding(
                                        vertical = 4.dp,
                                        horizontal = 8.dp,
                                    )
                            )
                        }
                    }
                }
            }
        }

        is DeeplinkPart.Text -> {
            Text(
                part.value,
                style = FloconTheme.typography.bodySmall.copy(
                    color = FloconTheme.colorPalette.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun DeeplinkTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    val isValueEmpty = value.isEmpty()
    Box(
        modifier = modifier.background(
            color = FloconTheme.colorPalette.primary,
            shape = RoundedCornerShape(2.dp),
        ).padding(horizontal = 2.dp, vertical = 2.dp)
            .width(IntrinsicSize.Min),
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
            modifier = Modifier.graphicsLayer {
                alpha = if (isValueEmpty) 1f else 0f
            },
        )

        BasicTextField(
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colorPalette.onSurface,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            value = value,
            cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
            onValueChange = onValueChange,
        )
    }
}

@Composable
@Preview
private fun DeeplinkItemViewPreview() {
    FloconTheme {
        DeeplinkItemView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            submit = { _, _ -> },
            item = previewDeeplinkViewState(),
            removeFromHistory = {},
        )
    }
}
