@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor
import kotlin.math.min
import kotlin.math.sin

@Composable
fun FloconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingComponent: @Composable (() -> Unit)? = null,
    trailingComponent: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = FloconTheme.typography.bodySmall,
    containerColor: Color = FloconTheme.colorPalette.surfaceVariant
) {
    val contentColor = FloconTheme.colorPalette.contentColorFor(containerColor)
    val colors = TextFieldDefaults.colors(
        focusedTextColor = contentColor,
        errorTextColor = contentColor,
        unfocusedTextColor = contentColor,
        disabledTextColor = contentColor,
        errorContainerColor = containerColor,
        focusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorPlaceholderColor = contentColor.copy(alpha = 0.6f),
        disabledPlaceholderColor = contentColor.copy(alpha = 0.6f),
        focusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
        errorLabelColor = FloconTheme.colorPalette.onSurface,
        disabledLabelColor = FloconTheme.colorPalette.onSurface,
        focusedLabelColor = FloconTheme.colorPalette.onSurface,
        unfocusedLabelColor = FloconTheme.colorPalette.onSurface
    )
    val interactionSource = remember { MutableInteractionSource() }
    val shape = FloconTheme.shapes.medium

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
            DecorationBox(
                value = value,
                innerTextField = it,
                interactionSource = interactionSource,
                enabled = enabled,
                trailingComponent = trailingComponent,
                leadingComponent = leadingComponent,
                prefix = prefix,
                suffix = suffix,
                singleLine = singleLine,
                isError = isError,
                placeholder = placeholder,
                colors = colors,
                shape = shape
            )
        },
        modifier = modifier
    )
}

@Composable
fun FloconTextField(
    value: String,
    label: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingComponent: @Composable (() -> Unit)? = null,
    trailingComponent: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = FloconTheme.typography.bodySmall,
    containerColor: Color = FloconTheme.colorPalette.surfaceVariant
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        label()
        FloconTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = textStyle,
            containerColor = containerColor,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            suffix = suffix,
            prefix = prefix,
            enabled = enabled,
            leadingComponent = leadingComponent,
            trailingComponent = trailingComponent,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FloconTextFieldWithoutM3(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingComponent: @Composable (() -> Unit)? = null,
    trailingComponent: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = FloconTheme.typography.bodySmall,
    containerColor: Color = FloconTheme.colorPalette.surfaceVariant
) {
    val contentColor = FloconTheme.colorPalette.contentColorFor(containerColor)
    val shape = FloconTheme.shapes.medium

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = contentColor),
        maxLines = maxLines,
        minLines = minLines,
        cursorBrush = SolidColor(Color.White), // TODO Light mod
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        singleLine = singleLine,
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(containerColor)
                    .padding(horizontal = 4.dp)
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    if (leadingComponent != null) {
                        leadingComponent()
                    }
                    if (prefix != null) {
                        prefix()
                    }
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (value.isEmpty() && placeholder != null) {
                            placeholder()
                        }
                        it()
                    }
                    if (suffix != null) {
                        suffix()
                    }
                    if (trailingComponent != null) {
                        trailingComponent()
                    }
                }
            }
        },
        modifier = modifier.heightIn(min = 30.dp)
    )
}

@Composable
private fun DecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    colors: TextFieldColors,
    shape: Shape,
    interactionSource: InteractionSource,
    placeholder: @Composable (() -> Unit)?,
    leadingComponent: @Composable (() -> Unit)?,
    trailingComponent: @Composable (() -> Unit)?,
    prefix: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    enabled: Boolean,
    singleLine: Boolean,
    isError: Boolean
) {
    TextFieldDefaults.DecorationBox(
        value = value,
        shape = shape,
        interactionSource = interactionSource,
        innerTextField = innerTextField,
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        colors = colors,
        visualTransformation = VisualTransformation.None,
        contentPadding = PaddingValues(),//PaddingValues(vertical = 4.dp, horizontal = 8.dp),
        container = {
            ContainerBox(
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors
            )
        },
        placeholder = placeholder,
        prefix = prefix,
        suffix = suffix,
        trailingIcon = trailingComponent,
        leadingIcon = leadingComponent
    )
}


@Composable
private fun ContainerBox(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    shape: Shape,
    colors: TextFieldColors
) {
    TextFieldDefaults.Container(
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@Composable
fun defaultLabel(text: String): @Composable () -> Unit = {
    DefaultLabel(text)
}

@Composable
fun DefaultLabel(text: String) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Thin),
    )
}

@Composable
fun defaultPlaceHolder(text: String?): @Composable (() -> Unit)? = if (text.isNullOrEmpty())
    null
else {
    {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall
        )
    }
}
