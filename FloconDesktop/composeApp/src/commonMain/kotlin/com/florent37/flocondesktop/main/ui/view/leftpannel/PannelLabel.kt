package com.florent37.flocondesktop.main.ui.view.leftpannel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconColorScheme

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
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Thin,
            color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
        ),
    )
}
