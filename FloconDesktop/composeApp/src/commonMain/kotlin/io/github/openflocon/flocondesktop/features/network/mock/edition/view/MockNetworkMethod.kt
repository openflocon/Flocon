package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconOutlinedPill

@Composable
fun MockNetworkMethod(
    method: MockNetworkMethodUi,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = FloconTheme.colors
    val color = when (method) {
        MockNetworkMethodUi.DELETE -> colors.catCrash
        MockNetworkMethodUi.GET -> colors.catNetwork
        MockNetworkMethodUi.PATCH -> colors.catDeeplink
        MockNetworkMethodUi.POST -> colors.catAnalytics
        MockNetworkMethodUi.PUT -> colors.accent
        MockNetworkMethodUi.ALL -> null
    }
    FloconOutlinedPill(
        text = method.text,
        color = color,
        modifier = modifier.then(
            if (onClick != null) Modifier.clip(FloconTheme.shapes.small).clickable(onClick = onClick) else Modifier
        ),
    )
}
