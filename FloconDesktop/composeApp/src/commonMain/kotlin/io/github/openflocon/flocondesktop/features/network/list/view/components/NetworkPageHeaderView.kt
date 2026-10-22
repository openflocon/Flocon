package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_network
import flocondesktop.composeapp.generated.resources.network_counter_requests
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.floconMonoStyle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NetworkPageHeaderView(
    requestCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)) {
            Text(
                text = stringResource(Res.string.menu_item_network),
                style = FloconTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
                maxLines = 1,
            )
            Text(
                text = "$requestCount ${pluralStringResource(Res.plurals.network_counter_requests, requestCount)}",
                style = floconMonoStyle(),
                color = colors.inkFaint,
                maxLines = 1,
            )
        }
        Spacer(Modifier.weight(1f))
        FeatureDocButton(doc = FeatureDoc.Network)
    }
}

@Composable
@Preview
private fun NetworkPageHeaderViewPreview() {
    FloconTheme {
        NetworkPageHeaderView(requestCount = 6)
    }
}
