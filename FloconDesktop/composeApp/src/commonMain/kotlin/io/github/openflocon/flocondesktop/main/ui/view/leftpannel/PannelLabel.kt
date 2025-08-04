package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconColorScheme
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun PannelLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(
                start = 12.dp,
                bottom = 4.dp,
            ),
        text = text,
        style = FloconTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Thin,
            color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
        ),
    )
}
