package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

@Composable
fun FilterBar(
    placeholderText: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterText by remember { mutableStateOf("") }
    val onTextChangedCallback by rememberUpdatedState(onTextChange)
    val displayClearButton by remember { derivedStateOf { filterText.isNotEmpty() } }

    FloconTextFieldWithoutM3(
        value = filterText,
        onValueChange = {
            filterText = it
            onTextChangedCallback(filterText)
        },
        placeholder = defaultPlaceHolder(placeholderText),
        leadingComponent = {
            FloconIcon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier.size(16.dp)
            )
        },
        containerColor = FloconTheme.colorPalette.secondary,
        trailingComponent = {
            AnimatedVisibility(
                visible = displayClearButton,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Delete,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            onClick = {
                                filterText = ""
                                onTextChangedCallback("")
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        },
        textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
        modifier = modifier
    )
}
