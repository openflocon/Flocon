package io.github.openflocon.flocondesktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon
import flocondesktop.composeapp.generated.resources.app_icon_small
import io.github.openflocon.flocondesktop.common.utils.openInBrowser
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_HEIGHT
import io.github.openflocon.flocondesktop.window.MIN_WINDOW_WIDTH
import io.github.openflocon.flocondesktop.window.WindowStateData
import io.github.openflocon.flocondesktop.window.WindowStateSaver
import io.github.openflocon.flocondesktop.window.size
import io.github.openflocon.flocondesktop.window.windowPosition
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextButton
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.awt.Dimension
import java.net.URI

fun main() {
    System.setProperty("apple.awt.application.name", "Flocon")

    return application {
        var openAbout by remember { mutableStateOf(false) }

        Desktop.getDesktop().setAboutHandler {
            openAbout = true
        }

        startKoinApp()
        setSingletonImageLoaderFactory { context ->
            ImageLoader
                .Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }.build()
        }

        val savedState = remember {
            WindowStateSaver.load()
        }

        val windowState = rememberWindowState(
            size = savedState.size(),
            position = savedState.windowPosition(),
        )

        Window(
            state = windowState,
            onCloseRequest = {
                val currentSize = windowState.size
                val currentPosition = windowState.position
                WindowStateSaver.save(
                    WindowStateData(
                        width = currentSize.width.value.toInt(),
                        height = currentSize.height.value.toInt(),
                        x = currentPosition.x.value.toInt(),
                        y = currentPosition.y.value.toInt(),
                    ),
                )

                exitApplication()
            },
            title = "Flocon",
            icon = painterResource(Res.drawable.app_icon_small), // Remove black behind icon
        ) {
            window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)

            App()


            if (openAbout) {
                AboutScreen(
                    onCloseRequest = { openAbout = false }
                )
            }
        }
    }
}

@Composable
private fun AboutScreen(
    onCloseRequest: () -> Unit
) {
    Window(
        title = "About",
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(Dp.Unspecified, Dp.Unspecified)
        )
    ) {
        FloconSurface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Text(
                    text = "Flocon",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSurface
                )
                FloconTextButton(
                    onClick = {
                        openInBrowser(URI.create("https://github.com/openflocon/Flocon"))
                    }
                ) {
                    Text(
                        text = "GitHub"
                    )
                }
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    contributors.forEach {
                        Contributor(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun Contributor(
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

@Immutable
private data class Contributor(
    val firstName: String,
    val lastName: String,
    val profile: String,
    val image: String
)

private val contributors = listOf(
    Contributor(
        firstName = "Florent",
        lastName = "Champigny",
        profile = "https://github.com/florent37",
        image = "https://avatars.githubusercontent.com/u/5754972?v=4"
    ),
    Contributor(
        firstName = "Raphael",
        lastName = "Teyssandier",
        profile = "https://github.com/doTTTTT",
        image = "https://avatars.githubusercontent.com/u/13266870?v=4"
    )
)
