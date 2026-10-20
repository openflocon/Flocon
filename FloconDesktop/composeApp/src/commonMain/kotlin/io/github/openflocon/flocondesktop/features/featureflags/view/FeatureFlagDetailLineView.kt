package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun FeatureFlagDetailLineView(
    label: String,
    value: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 8.dp)
                .width(labelWidth),
        )
        Text(
            text = value,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 6.dp, horizontal = 8.dp),
        )
    }
}
