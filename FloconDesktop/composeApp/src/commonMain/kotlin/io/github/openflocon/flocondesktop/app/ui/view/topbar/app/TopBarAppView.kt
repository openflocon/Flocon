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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
internal fun TopBarAppView(
    deviceApp: DeviceAppUiModel,
    modifier: Modifier = Modifier,
    platform: DeviceItemUiModel.Platform,
    selected: Boolean = false,
    deleteClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppImage(
            deviceApp = deviceApp,
            platform = platform,
            modifier = Modifier.size(24.dp),
        )
        Column {
            Text(
                text = deviceApp.name,
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = deviceApp.packageName,
                style = FloconTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                ),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.8f),
            )
        }
        if (!selected && deleteClick != null) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(2.dp)
                    .clickable(onClick = deleteClick),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Close,
                    tint = FloconTheme.colorPalette.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
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
        when (platform) {
            DeviceItemUiModel.Platform.Desktop -> {
                Image(
                    imageVector = Icons.Default.Terminal,
                    colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary),
                    contentDescription = null,
                    modifier = modifier,
                )
            }

            DeviceItemUiModel.Platform.ios,
            DeviceItemUiModel.Platform.Android,
            DeviceItemUiModel.Platform.Unknown -> {
                // Fallback : affiche une icône par défaut si iconEncoded est null ou invalide
                Image(
                    painter = painterResource(Res.drawable.smartphone),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
        }
    }
}
