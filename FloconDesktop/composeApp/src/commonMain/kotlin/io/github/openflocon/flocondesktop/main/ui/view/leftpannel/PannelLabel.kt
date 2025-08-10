package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun PannelLabel(
    text: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = expanded,
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (it) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 4.dp),
                    text = text,
                    style = FloconTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Thin,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.5f),
                    ),
                )
            } else {
                HorizontalDivider()
            }
        }
    }
}
