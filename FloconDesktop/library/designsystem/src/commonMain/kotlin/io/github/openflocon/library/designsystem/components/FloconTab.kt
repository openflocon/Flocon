package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconTab(
    text: String,
    isSelected: Boolean,
    onSelected: ()-> Unit,
    tabType: TabType,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(
                when(tabType) {
                    TabType.Start -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    TabType.Middle -> RectangleShape
                    TabType.End -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                }
            )
            .background(
                color = if (isSelected) {
                    Color.White.copy(alpha = 0.8f)
                } else {
                    Color.White.copy(alpha = 0.1f)
                },
            ).clickable {
                onSelected()
            }.padding(vertical = 8.dp),
        text = text,
        textAlign = TextAlign.Center,
        style = FloconTheme.typography.bodyMedium,
        color = if (isSelected) {
            FloconTheme.colorPalette.primary
        } else {
            FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f)
        },
    )
}

enum class TabType {
    Start,
    Middle,
    End
}
