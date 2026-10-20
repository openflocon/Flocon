package io.github.openflocon.flocondesktop.telemetry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.telemetry_allow_full
import flocondesktop.composeapp.generated.resources.telemetry_consent_message
import flocondesktop.composeapp.generated.resources.telemetry_disable
import flocondesktop.composeapp.generated.resources.telemetry_learn_more
import flocondesktop.composeapp.generated.resources.telemetry_stay_anonymous
import flocondesktop.composeapp.generated.resources.telemetry_title
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

const val TELEMETRY_DOC_URL = "https://openflocon.github.io/Flocon/telemetry/"

/** First-launch consent card. Only displayed on official builds, until the user picks a level. */
@Composable
internal fun BoxScope.TelemetryConsentView() {
    val viewModel = koinViewModel<TelemetryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.shouldAskConsent) {
        TelemetryConsentCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onLevelSelected = viewModel::setLevel,
        )
    }
}

@Composable
private fun TelemetryConsentCard(
    onLevelSelected: (TelemetryLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .width(420.dp)
            .clickable(onClick = { /* intercept clicks behind */ })
            .background(color = FloconTheme.colorPalette.tertiary, shape = FloconTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = stringResource(Res.string.telemetry_title),
            color = FloconTheme.colorPalette.onTertiary,
            style = FloconTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.telemetry_consent_message),
            color = FloconTheme.colorPalette.onTertiary,
            style = FloconTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.telemetry_learn_more),
            color = FloconTheme.colorPalette.onTertiary,
            style = FloconTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable { uriHandler.openUri(TELEMETRY_DOC_URL) },
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconButton(
                containerColor = FloconTheme.colorPalette.primary,
                onClick = { onLevelSelected(TelemetryLevel.Full) },
            ) {
                Text(stringResource(Res.string.telemetry_allow_full), color = FloconTheme.colorPalette.onPrimary)
            }
            FloconOutlinedButton(
                borderColor = FloconTheme.colorPalette.primary,
                onClick = { onLevelSelected(TelemetryLevel.Anonymous) },
            ) {
                Text(stringResource(Res.string.telemetry_stay_anonymous), color = FloconTheme.colorPalette.primary)
            }
            FloconOutlinedButton(
                borderColor = FloconTheme.colorPalette.primary,
                onClick = { onLevelSelected(TelemetryLevel.Disabled) },
            ) {
                Text(stringResource(Res.string.telemetry_disable), color = FloconTheme.colorPalette.primary)
            }
        }
    }
}
