package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun NetworkMockFieldView(
    label: String,
    placeHolder: String?,
    value: String,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MockNetworkLabelView(label)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                placeHolder?.takeIf { value.isEmpty() }?.let {
                    Text(
                        text = it,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.35f),
                    )
                }
                BasicTextField(
                    textStyle = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    minLines = minLines,
                    maxLines = maxLines,
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                    onValueChange = {
                        onValueChange(it)
                    },
                )
            }
        }
    }
}

