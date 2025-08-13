@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    textStyle: TextStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
) {
    val colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = value,
                shape = shape,
                interactionSource = interactionSource,
                innerTextField = it,
                enabled = enabled,
                isError = isError,
                singleLine = singleLine,
                colors = colors,
                visualTransformation = VisualTransformation.None,
                container = {
                    TextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        shape = shape,
                        colors = colors
                    )
                },
                placeholder = placeholder
            )
        },
        modifier = modifier
    )
}

@Composable
fun FloconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    textStyle: TextStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
) {
    FloconTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = if (placeholderText != null) {
            {
                Text(
                    text = placeholderText
                )
            }
        } else {
            null
        },
        enabled = enabled,
        singleLine = singleLine,
        isError = isError,
        textStyle = textStyle
    )
}

@Composable
fun FloconSmallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    textStyle: TextStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
    placeHolderStyle: TextStyle = FloconTheme.typography.bodySmall.copy(
        color = FloconTheme.colorPalette.onSurfaceVariant.copy(alpha = 0.4f),
    ),
) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) {
            Text(
                text = placeHolder,
                style = placeHolderStyle,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(FloconTheme.colorPalette.primary),
        )
    }
}
