package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_copy
import flocondesktop.composeapp.generated.resources.topbar_connect_by_url
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * The urls of this machine, to copy into `FloconConnectionMode.Url` in an app.
 */
@Composable
internal fun TopBarLocalUrls(
    urls: ImmutableList<String>,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(FloconTheme.sizes.deviceDropdownWidth),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Text(
            text = stringResource(Res.string.topbar_connect_by_url),
            color = FloconTheme.colors.inkFaint,
            style = FloconTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
        )
        urls.forEach { url ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.small)
                    .background(FloconTheme.colors.bg200)
                    .clickable { onCopy(url) }
                    .padding(FloconTheme.spacing.small),
            ) {
                Text(
                    text = url,
                    color = FloconTheme.colors.ink,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.general_copy),
                    color = FloconTheme.colors.accent,
                    style = FloconTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopBarLocalUrlsPreview() {
    FloconTheme {
        TopBarLocalUrls(
            urls = persistentListOf("ws://192.168.1.20:9023", "ws://10.0.0.4:9023"),
            onCopy = {},
        )
    }
}
