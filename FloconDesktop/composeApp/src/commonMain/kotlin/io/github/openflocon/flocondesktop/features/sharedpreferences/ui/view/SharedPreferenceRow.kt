package io.github.openflocon.flocondesktop.features.sharedpreferences.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewSharedPreferencesBooleanRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewSharedPreferencesFloatRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewSharedPreferencesIntRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewSharedPreferencesLongRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewSharedPreferencesStringRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SharedPreferenceRowView(
    model: SharedPreferencesRowUiModel,
    onValueChanged: (row: SharedPreferencesRowUiModel, valueString: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 8.dp,
                vertical = 2.dp,
            ) // Padding for the entire item
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            model.key,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        )

        when (model.value) {
            is SharedPreferencesRowUiModel.Value.BooleanValue -> {
                var value by remember(model.value) { mutableStateOf(model.value.value) }

                Box(
                    Modifier
                        .weight(1f)
                        .background(color = FloconTheme.colorPalette.panel, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Checkbox(
                        modifier = Modifier.height(36.dp),
                        checked = value,
                        onCheckedChange = {
                            value = it
                            onValueChanged(model, it.toString())
                        },
                    )
                }
            }

            is SharedPreferencesRowUiModel.Value.FloatValue -> {
                var value by remember(model.value) { mutableStateOf(model.value.value.toString()) }
                Row(
                    Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = {
                            value = it
                        },
                        textStyle = FloconTheme.typography.bodySmall.copy(
                            color = FloconTheme.colorPalette.onSurface,
                        ),
                        cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = FloconTheme.colorPalette.panel,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(all = 10.dp),
                    )

                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value)
                        },
                    )
                }
            }

            is SharedPreferencesRowUiModel.Value.IntValue -> {
                var value by remember(model.value) { mutableIntStateOf(model.value.value) }
                Row(
                    Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BasicTextField(
                        value = value.toString(),
                        onValueChange = {
                            try {
                                value = it.toInt()
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        },
                        textStyle = FloconTheme.typography.bodySmall.copy(
                            color = FloconTheme.colorPalette.onSurface,
                        ),
                        cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = FloconTheme.colorPalette.panel,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(all = 10.dp),
                    )

                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value.toString())
                        },
                    )
                }
            }

            is SharedPreferencesRowUiModel.Value.LongValue -> {
                var value by remember(model.value) { mutableLongStateOf(model.value.value) }
                Row(
                    Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BasicTextField(
                        value = value.toString(),
                        onValueChange = {
                            try {
                                value = it.toLong()
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        },
                        cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                        textStyle = FloconTheme.typography.bodySmall.copy(
                            color = FloconTheme.colorPalette.onSurface,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = FloconTheme.colorPalette.panel,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(all = 10.dp),
                    )

                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value.toString())
                        },
                    )
                }
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
                var value by remember(model.value) { mutableStateOf(model.value.value) }
                Row(
                    Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = {
                            value = it
                        },
                        cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                        textStyle = FloconTheme.typography.bodySmall.copy(
                            color = FloconTheme.colorPalette.onSurface,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = FloconTheme.colorPalette.panel,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(all = 10.dp),
                    )
                    SharedPreferenceSendButton(
                        onClick = {
                            onValueChanged(model, value)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedPreferenceSendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Outlined.Send
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(all = 10.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.fillMaxSize(),
        )
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
        )
    }
}
