package io.github.openflocon.flocondesktop.about

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import io.github.openflocon.flocondesktop.common.utils.openInBrowser
import io.github.openflocon.library.designsystem.FloconTheme
import java.net.URI

@Composable
internal fun ContributorView(
    contributor: Contributor
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor = if (hovered) FloconTheme.colorPalette.onSurface else Color.Transparent
    val shape = RoundedCornerShape(10.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .clip(shape)
            .border(width = 2.dp, color = borderColor, shape = shape)
            .clickable(
                onClick = { openInBrowser(URI.create(contributor.profile)) },
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(contributor.image)
                .listener(onError = { _, error ->
                    error.throwable.printStackTrace()
                })
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(shape)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = contributor.firstName,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = contributor.lastName,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
