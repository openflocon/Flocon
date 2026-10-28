package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.network_image_response
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Tells that the response of a call is an image, in the network list and in the timeline.
 */
@Composable
fun NetworkImageBadge(modifier: Modifier = Modifier) {
    val color = FloconTheme.colors.catNetwork
    Box(
        modifier = modifier
            .size(width = 20.dp, height = 18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = stringResource(Res.string.network_image_response),
            tint = color,
            modifier = Modifier.size(12.dp),
        )
    }
}
