package io.github.openflocon.flocondesktop.telemetry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.telemetry_allow_full
import flocondesktop.composeapp.generated.resources.telemetry_consent_hint
import flocondesktop.composeapp.generated.resources.telemetry_consent_message
import flocondesktop.composeapp.generated.resources.telemetry_consent_privacy
import flocondesktop.composeapp.generated.resources.telemetry_disable
import flocondesktop.composeapp.generated.resources.telemetry_learn_more
import flocondesktop.composeapp.generated.resources.telemetry_share_anonymous
import flocondesktop.composeapp.generated.resources.telemetry_title
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

const val TELEMETRY_DOC_URL = "https://github.com/openflocon/Flocon/blob/main/docs2/telemetry.md"

private val CardWidth = 460.dp
private val CardShape = RoundedCornerShape(12.dp)

/** First-launch consent card. Only displayed on official builds, until the user picks a level: nothing is sent before. */
@Composable
internal fun TelemetryConsentCard(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<TelemetryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.shouldAskConsent) {
        TelemetryConsentCard(
            onLevelSelected = viewModel::setLevel,
            modifier = modifier,
        )
    }
}

@Composable
internal fun TelemetryConsentCard(
    onLevelSelected: (TelemetryLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing

    // Opaque `floating` surface: the translucent glass backgrounds let the content behind show through the text.
    FloconSurface(
        color = colors.floating,
        contentColor = colors.ink,
        shape = CardShape,
        border = BorderStroke(1.dp, colors.lineStrong),
        shadowElevation = 16.dp,
        modifier = modifier.width(CardWidth),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier.padding(spacing.large),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Insights,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(Res.string.telemetry_title),
                    color = colors.ink,
                    style = FloconTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Text(
                text = stringResource(Res.string.telemetry_consent_message),
                color = colors.inkMuted,
                style = FloconTheme.typography.bodySmall,
            )
            val privacy = stringResource(Res.string.telemetry_consent_privacy)
            val learnMore = stringResource(Res.string.telemetry_learn_more)
            Text(
                text = buildAnnotatedString {
                    append(privacy)
                    append(" ")
                    withStyle(SpanStyle(color = colors.accent)) { append(learnMore) }
                },
                color = colors.inkMuted,
                style = FloconTheme.typography.bodySmall,
                modifier = Modifier.clickable { uriHandler.openUri(TELEMETRY_DOC_URL) },
            )
            // Same style and width for every choice: refusing must be as easy as accepting.
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                modifier = Modifier.padding(top = spacing.extraSmall),
            ) {
                ConsentButton(
                    text = stringResource(Res.string.telemetry_disable),
                    onClick = { onLevelSelected(TelemetryLevel.Disabled) },
                    modifier = Modifier.weight(1f),
                )
                ConsentButton(
                    text = stringResource(Res.string.telemetry_share_anonymous),
                    onClick = { onLevelSelected(TelemetryLevel.Anonymous) },
                    modifier = Modifier.weight(1f),
                )
                ConsentButton(
                    text = stringResource(Res.string.telemetry_allow_full),
                    onClick = { onLevelSelected(TelemetryLevel.Full) },
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = stringResource(Res.string.telemetry_consent_hint),
                color = colors.inkFaint,
                style = FloconTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ConsentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconOutlinedButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = FloconTheme.spacing.extraSmall),
        )
    }
}

@Preview
@Composable
private fun TelemetryConsentCardPreview() {
    FloconTheme {
        TelemetryConsentCard(onLevelSelected = {})
    }
}
