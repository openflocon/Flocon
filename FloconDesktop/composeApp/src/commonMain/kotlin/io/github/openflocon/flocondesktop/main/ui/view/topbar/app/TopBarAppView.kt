package io.github.openflocon.flocondesktop.main.ui.view.topbar


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64


@Composable
internal fun TopBarAppView(
    deviceApp: DeviceAppUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBarSelector(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppImage(
                deviceApp = deviceApp,
                modifier = Modifier.size(24.dp),
            )
            Column {
                Text(
                    text = deviceApp.name,
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = FloconTheme.colorPalette.onPanel,
                )
                Text(
                    text = deviceApp.packageName,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                    ),
                    color = FloconTheme.colorPalette.onPanel.copy(alpha = 0.8f),
                )
            }
        }
    }
}


@Composable
private fun AppImage(
    deviceApp: DeviceAppUiModel,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(deviceApp.iconEncoded) {
        deviceApp.iconEncoded?.let { encoded ->
            try {
                val decodedBytes = Base64.decode(encoded) //, Base64.DEFAULT)
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
        // Fallback : affiche une icône par défaut si iconEncoded est null ou invalide
        Image(
            painter = painterResource(Res.drawable.smartphone),
            contentDescription = null,
            modifier = modifier,
        )
    }
}
