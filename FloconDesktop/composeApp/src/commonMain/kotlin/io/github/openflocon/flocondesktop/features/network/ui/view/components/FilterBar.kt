package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FilterBar(
    placeholderText: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterText by remember { mutableStateOf("") }
    val onTextChangedCallback by rememberUpdatedState(onTextChange)
    val displayClearButton by remember { derivedStateOf { filterText.isNotEmpty() } }
    val displayPlaceholder by remember { derivedStateOf { filterText.isEmpty() } }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Custom styled BasicTextField
        Box(
            modifier =
            Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FloconTheme.colorPalette.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (displayPlaceholder) {
                Text(
                    text = placeholderText,
                    style =
                    FloconTheme.typography.bodySmall.copy(
                        color =
                        FloconTheme.colorPalette.onSurfaceVariant.copy(
                            alpha = 0.4f,
                        ),
                    ),
                )
            }
            BasicTextField(
                value = filterText,
                onValueChange = {
                    filterText = it
                    onTextChangedCallback(filterText)
                },
                singleLine = true,
                textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(FloconTheme.colorPalette.primary),
            )
        }

        // Animated visibility for Clear button
        AnimatedVisibility(
            visible = displayClearButton,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            Text(
                text = "Clear",
                modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        filterText = ""
                        onTextChangedCallback("")
                    }.padding(horizontal = 12.dp),
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
                style = FloconTheme.typography.titleMedium,
            )
        }
    }
}
