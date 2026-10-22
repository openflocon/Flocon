package io.github.openflocon.flocondesktop.app.ui.view.topbar.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64

@Composable
internal fun TopBarApp(
    deviceApp: DeviceAppUiModel,
    modifier: Modifier = Modifier,
    platform: DeviceItemUiModel.Platform,
    selected: Boolean = false,
    deleteClick: (() -> Unit)? = null,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier.padding(
            start = FloconTheme.spacing.small,
            end = FloconTheme.spacing.extraSmall,
            top = FloconTheme.spacing.small,
            bottom = FloconTheme.spacing.small
        ),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppImage(
            deviceApp = deviceApp,
            platform = platform,
            modifier = Modifier
                .size(32.dp)
                .clip(FloconTheme.shapes.medium),
        )
        Column {
            Text(
                text = deviceApp.name,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.ink,
            )
            // the package is technical data
            Text(
                text = deviceApp.packageName,
                style = FloconTheme.typography.monoSmall,
                color = colors.inkMuted,
                maxLines = 1,
            )
        }
        if (!selected && deleteClick != null) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(FloconTheme.shapes.small)
                    .background(colors.bg300)
                    .padding(FloconTheme.spacing.tiny)
                    .clickable(onClick = deleteClick),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Close,
                    tint = colors.inkMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        if (selected) {
            Spacer(modifier = Modifier.weight(1f))
            FloconIcon(
                imageVector = Icons.Outlined.Check,
                tint = colors.accent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun AppImage(
    deviceApp: DeviceAppUiModel,
    modifier: Modifier = Modifier,
    platform: DeviceItemUiModel.Platform
) {
    val imageBitmap = remember(deviceApp.iconEncoded) {
        deviceApp.iconEncoded?.let { encoded ->
            try {
                val decodedBytes = Base64.decode(encoded) // , Base64.DEFAULT)
                Image.makeFromEncoded(decodedBytes).toComposeImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
        )
    } else {
        // no icon: a soft square, like the device
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(FloconTheme.colors.catTableSoft),
        ) {
            when (platform) {
                DeviceItemUiModel.Platform.Desktop -> {
                    FloconIcon(
                        imageVector = Icons.Default.Terminal,
                        tint = FloconTheme.colors.catTable,
                        modifier = Modifier.size(18.dp),
                    )
                }

                DeviceItemUiModel.Platform.ios,
                DeviceItemUiModel.Platform.Android,
                DeviceItemUiModel.Platform.Unknown -> {
                    Image(
                        painter = painterResource(Res.drawable.smartphone),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
