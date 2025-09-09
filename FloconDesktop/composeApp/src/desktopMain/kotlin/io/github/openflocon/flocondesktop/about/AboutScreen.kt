package io.github.openflocon.flocondesktop.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon
import io.github.openflocon.flocondesktop.BuildConfig
import io.github.openflocon.flocondesktop.common.utils.openInBrowser
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextButton
import org.jetbrains.compose.resources.painterResource
import java.net.URI

@Composable
internal fun AboutScreen(
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
                Text(
                    text = BuildConfig.APP_VERSION,
                    style = FloconTheme.typography.labelSmall,
                    color = FloconTheme.colorPalette.onSurface
                )
                FloconTextButton(
                    onClick = {
                        openInBrowser(URI.create("https://github.com/openflocon/Flocon"))
                    },
                    containerColor = FloconTheme.colorPalette.secondary
                ) {
                    Text(
                        text = "GitHub"
                    )
                }
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    contributors.fastForEach {
                        ContributorView(it)
                    }
                }
            }
        }
    }
}
