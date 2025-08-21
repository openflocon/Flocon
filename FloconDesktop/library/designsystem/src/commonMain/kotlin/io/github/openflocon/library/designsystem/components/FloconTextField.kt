@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = FloconTheme.typography.bodySmall
) {
    val contentColor = FloconTheme.colorPalette.onSurfaceVariant
    val colors = TextFieldDefaults.colors(
        focusedTextColor = contentColor,
        errorTextColor = contentColor,
        unfocusedTextColor = contentColor,
        disabledTextColor = contentColor,
        errorContainerColor = FloconTheme.colorPalette.surfaceVariant,
        focusedContainerColor = FloconTheme.colorPalette.surfaceVariant,
        disabledContainerColor = FloconTheme.colorPalette.surfaceVariant,
        unfocusedContainerColor = FloconTheme.colorPalette.surfaceVariant,
        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorPlaceholderColor = contentColor.copy(alpha = 0.8f),
        disabledPlaceholderColor = contentColor.copy(alpha = 0.8f),
        focusedPlaceholderColor = contentColor.copy(alpha = 0.8f),
        unfocusedPlaceholderColor = contentColor.copy(alpha = 0.8f)
    )
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(10.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = contentColor), // Fix style
        maxLines = maxLines,
        minLines = minLines,
        cursorBrush = SolidColor(Color.White), // TODO Light mod
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
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
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
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
fun placeHolder(text: String?): @Composable (() -> Unit)? = if (text.isNullOrEmpty())
    null
else {
    {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall
        )
    }
}
