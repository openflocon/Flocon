package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesBooleanRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesFloatRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesIntRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesLongRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesStringRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconTextField

@Composable
fun SharedPreferenceRowView(
    model: SharedPreferencesRowUiModel,
    onValueChanged: (row: SharedPreferencesRowUiModel, valueString: String) -> Unit,
    onEditClicked: (row: SharedPreferencesRowUiModel, stringValue: SharedPreferencesRowUiModel.Value.StringValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = FloconTheme.colorPalette.secondary
    val textFieldBackgroundColor = FloconTheme.colorPalette.surface

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = borderColor,
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            model.key,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 6.dp),
        )

        VerticalDivider(
            color = borderColor,
            thickness = 1.dp,
        )

        Row(
            Modifier
                .weight(1f)
                .padding(start = 6.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (model.value) {
                is SharedPreferencesRowUiModel.Value.BooleanValue -> {
                    var value by remember(model.value) { mutableStateOf(model.value.value) }

                    FloconCheckbox(
                        modifier = Modifier.height(36.dp),
                        checked = value,
                        uncheckedColor = FloconTheme.colorPalette.secondary,
                        onCheckedChange = {
                            value = it
                            onValueChanged(model, it.toString())
                        },
                    )
                }

                is SharedPreferencesRowUiModel.Value.FloatValue -> {
                    var value by remember(model.value) { mutableStateOf(model.value.value.toString()) }
                    FloconTextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.weight(1f),
                        containerColor = textFieldBackgroundColor,
                    )
                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value)
                        },
                    )
                }

                is SharedPreferencesRowUiModel.Value.IntValue -> {
                    var value by remember(model.value) { mutableIntStateOf(model.value.value) }
                    FloconTextField(
                        value = value.toString(),
                        containerColor = textFieldBackgroundColor,
                        onValueChange = {
                            try {
                                value = it.toInt()
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value.toString())
                        },
                    )
                }

                is SharedPreferencesRowUiModel.Value.LongValue -> {
                    var value by remember(model.value) { mutableLongStateOf(model.value.value) }

                    FloconTextField(
                        modifier = Modifier.weight(1f),
                        value = value.toString(),
                        containerColor = textFieldBackgroundColor,
                        onValueChange = {
                            try {
                                value = it.toLong()
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        }
                    )

                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value.toString())
                        },
                    )
                }

                is SharedPreferencesRowUiModel.Value.StringSetValue -> {
                    // no editable
                    Text(
                        model.value.value.toString(),
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 4.dp),
                    )
                }

                is SharedPreferencesRowUiModel.Value.StringValue -> {
                    val textValue = model.value.value
                    if (textValue.length > 80 || textValue.contains("\n")) {
                        Text(
                            text = textValue,
                            maxLines = 5,
                            style = FloconTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                                .background(
                                    textFieldBackgroundColor,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                        SharedPreferenceSendButton(
                            icon = Icons.Default.Edit,
                            onClick = {
                                onEditClicked(model, model.value)
                            },
                        )
                    } else {
                        var value by remember(model.value) { mutableStateOf(model.value.value) }
                        FloconTextField(
                            value = value,
                            onValueChange = { value = it },
                            textStyle = FloconTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            containerColor = textFieldBackgroundColor,
                        )

                        SharedPreferenceSendButton(
                            onClick = { onValueChanged(model, value) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedPreferenceSendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Outlined.Send,
) {
    FloconIconTonalButton(
        onClick = onClick,
        containerColor = FloconTheme.colorPalette.tertiary,
        modifier = modifier
    ) {
        FloconIcon(imageVector = icon)
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview() {
    FloconTheme {
        SharedPreferenceRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewSharedPreferencesStringRowUiModel(),
            onValueChanged = { _, _ -> },
            onEditClicked = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun SharedPreferenceRowPreview_int() {
    FloconTheme {
        SharedPreferenceRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewSharedPreferencesIntRowUiModel(),
            onValueChanged = { _, _ -> },
            onEditClicked = { _, _ -> },
        )
    }
}

@Preview()
@Composable
private fun SharedPreferenceRowPreview_float() {
    FloconTheme {
        SharedPreferenceRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewSharedPreferencesFloatRowUiModel(),
            onValueChanged = { _, _ -> },
            onEditClicked = { _, _ -> },
        )
    }
}

@Preview()
@Composable
private fun SharedPreferenceRowPreview_boolean() {
    FloconTheme {
        SharedPreferenceRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewSharedPreferencesBooleanRowUiModel(),
            onValueChanged = { _, _ -> },
            onEditClicked = { _, _ -> },
        )
    }
}

@Preview()
@Composable
private fun SharedPreferenceRowPreview_long() {
    FloconTheme {
        SharedPreferenceRowView(
            modifier = Modifier.fillMaxWidth(),
            model = previewSharedPreferencesLongRowUiModel(),
            onValueChanged = { _, _ -> },
            onEditClicked = { _, _ -> },
        )
    }
}
