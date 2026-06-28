package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun LeftPannelDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = FloconTheme.spacing.extraSmall),
        thickness = 1.dp,
        color = Color.Gray, // TODO Change
    )
}
