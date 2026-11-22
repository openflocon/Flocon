package io.github.openflocon.flocondesktop.features.proxy.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.proxy_android_step_app
import flocondesktop.composeapp.generated.resources.proxy_android_step_download
import flocondesktop.composeapp.generated.resources.proxy_android_step_install
import flocondesktop.composeapp.generated.resources.proxy_android_step_wifi
import flocondesktop.composeapp.generated.resources.proxy_binary_not_found
import flocondesktop.composeapp.generated.resources.proxy_certificate_page
import flocondesktop.composeapp.generated.resources.proxy_copy
import flocondesktop.composeapp.generated.resources.proxy_devices_hint
import flocondesktop.composeapp.generated.resources.proxy_experimental
import flocondesktop.composeapp.generated.resources.proxy_failed
import flocondesktop.composeapp.generated.resources.proxy_intro
import flocondesktop.composeapp.generated.resources.proxy_ios_step_download
import flocondesktop.composeapp.generated.resources.proxy_ios_step_install
import flocondesktop.composeapp.generated.resources.proxy_ios_step_trust
import flocondesktop.composeapp.generated.resources.proxy_ios_step_wifi
import flocondesktop.composeapp.generated.resources.proxy_no_address
import flocondesktop.composeapp.generated.resources.proxy_off
import flocondesktop.composeapp.generated.resources.proxy_once_hint
import flocondesktop.composeapp.generated.resources.proxy_pick_network
import flocondesktop.composeapp.generated.resources.proxy_platform_android
import flocondesktop.composeapp.generated.resources.proxy_platform_ios
import flocondesktop.composeapp.generated.resources.proxy_port
import flocondesktop.composeapp.generated.resources.proxy_server
import flocondesktop.composeapp.generated.resources.proxy_starting
import flocondesktop.composeapp.generated.resources.proxy_title
import flocondesktop.composeapp.generated.resources.proxy_wifi_values
import io.github.openflocon.flocondesktop.features.connect.view.QrCodeImage
import io.github.openflocon.flocondesktop.features.proxy.ProxyViewModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.previewProxyUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconMascotDialog
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import io.github.openflocon.library.designsystem.components.FloconSegmentedTextToggle
import io.github.openflocon.library.designsystem.components.FloconSwitch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val DialogWidth = 720.dp
private val QrSize = 180.dp
private val LabelWidth = 56.dp
private val StepNumberSize = 20.dp

// code, not translated
private const val USER_CERTIFICATES_SNIPPET = """<debug-overrides>
  <trust-anchors>
    <certificates src="user" />
  </trust-anchors>
</debug-overrides>"""

/** Starts / stops the proxy and walks the user through the setup of the phone. */
@Composable
fun ProxyDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProxyViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProxyDialog(
        uiState = uiState,
        onEnabledChange = viewModel::onEnabledChange,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}

@Composable
private fun ProxyDialog(
    uiState: ProxyUiState,
    onEnabledChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconMascotDialog(
        onDismissRequest = onDismissRequest,
        width = DialogWidth,
        mascotEnabled = uiState.mascotEnabled,
        mascotPawsY = ProxyMascotPeekPawsY,
        mascot = { ProxyMascotPeek(mood = uiState.mood, modifier = it) },
        modifier = modifier,
    ) {
        Column {
            Header(
                enabled = uiState.enabled,
                mood = uiState.mood,
                codeRain = uiState.mascotEnabled,
                onEnabledChange = onEnabledChange,
                onClose = onDismissRequest,
            )
            Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
            Column(
                modifier = Modifier.padding(FloconTheme.spacing.extraLarge),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
            ) {
                Text(
                    text = stringResource(Res.string.proxy_intro),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.inkMuted,
                )
                when (val status = uiState.status) {
                    ProxyStatusUiModel.Off -> Message(text = stringResource(Res.string.proxy_off))

                    ProxyStatusUiModel.Starting -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                    ) {
                        FloconCircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Message(text = stringResource(Res.string.proxy_starting))
                    }

                    ProxyStatusUiModel.BinaryNotFound -> Message(
                        text = stringResource(Res.string.proxy_binary_not_found),
                        color = FloconTheme.colors.statusWarning,
                    )

                    is ProxyStatusUiModel.Failed -> Message(
                        text = stringResource(Res.string.proxy_failed, status.message),
                        color = FloconTheme.colors.statusError,
                    )

                    is ProxyStatusUiModel.Running -> if (status.addresses.isEmpty()) {
                        Message(text = stringResource(Res.string.proxy_no_address))
                    } else {
                        PhoneSetup(addresses = status.addresses)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    enabled: Boolean,
    mood: ProxyMoodUiModel,
    codeRain: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = FloconTheme.colors.bg000
    // a hint of the rain's color in the bar, solid behind the title and the badge so they stay readable
    val barColor = when {
        !codeRain -> base
        mood == ProxyMoodUiModel.Watching -> FloconPixelPalette.Neon.copy(alpha = 0.04f).compositeOver(base)
        mood == ProxyMoodUiModel.Glitched -> FloconPixelPalette.Red.copy(alpha = 0.06f).compositeOver(base)
        else -> base
    }
    Box(modifier = modifier.fillMaxWidth().background(barColor)) {
        if (codeRain) {
            ProxyCodeRain(
                color = if (mood == ProxyMoodUiModel.Glitched) FloconPixelPalette.Red else FloconPixelPalette.Neon,
                headColor = if (mood == ProxyMoodUiModel.Watching) FloconPixelPalette.NeonHead else null,
                animated = mood != ProxyMoodUiModel.Asleep,
                strength = if (mood == ProxyMoodUiModel.Asleep) 0.22f else 1f,
                modifier = Modifier.matchParentSize(),
            )
        }
        HeaderContent(
            enabled = enabled,
            labelBackground = barColor.copy(alpha = 0.85f),
            onEnabledChange = onEnabledChange,
            onClose = onClose,
        )
    }
}

@Composable
private fun HeaderContent(
    enabled: Boolean,
    labelBackground: Color,
    onEnabledChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = FloconTheme.spacing.large, end = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.proxy_title),
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .background(labelBackground, FloconTheme.shapes.small)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        )
        Text(
            text = stringResource(Res.string.proxy_experimental).uppercase(),
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.accent,
            modifier = Modifier
                .background(FloconTheme.colors.accentSoft.compositeOver(labelBackground.copy(alpha = 1f)), FloconTheme.shapes.small)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        )
        Box(Modifier.weight(1f))
        FloconSwitch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
        )
        FloconIconButton(onClick = onClose, modifier = Modifier.padding(vertical = FloconTheme.spacing.small)) {
            FloconIcon(Icons.Outlined.Close)
        }
    }
}

@Composable
private fun Message(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colors.ink,
) {
    Text(
        text = text,
        style = FloconTheme.typography.bodyMedium,
        color = color,
        modifier = modifier,
    )
}

@Composable
private fun PhoneSetup(
    addresses: ImmutableList<ProxyAddressUiModel>,
    modifier: Modifier = Modifier,
) {
    var selectedIp by remember { mutableStateOf<String?>(null) }
    val selected = addresses.firstOrNull { it.ip == selectedIp } ?: addresses.first()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        if (addresses.size > 1) {
            NetworkPicker(
                addresses = addresses,
                selectedIp = selected.ip,
                onSelect = { selectedIp = it },
            )
        }
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
        ) {
            ProxyValues(address = selected)
            Box(Modifier.fillMaxHeight().width(1.dp).background(FloconTheme.colors.line))
            PlatformSteps(modifier = Modifier.weight(1f))
        }
        Text(
            text = stringResource(Res.string.proxy_devices_hint),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
    }
}

@Composable
private fun NetworkPicker(
    addresses: ImmutableList<ProxyAddressUiModel>,
    selectedIp: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = stringResource(Res.string.proxy_pick_network),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
        addresses.forEach { address ->
            val selected = address.ip == selectedIp
            val shape = FloconTheme.shapes.medium
            Text(
                text = address.ip,
                style = FloconTheme.typography.monoMedium,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
                modifier = Modifier
                    .clip(shape)
                    .background(if (selected) FloconTheme.colors.accentSoft else FloconTheme.colors.bg100)
                    .border(1.dp, if (selected) FloconTheme.colors.accent.copy(alpha = 0.4f) else FloconTheme.colors.line, shape)
                    .clickable { onSelect(address.ip) }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.extraSmall),
            )
        }
    }
}

/** What the phone needs: the Wi-Fi proxy values and the QR code to the certificate page. */
@Composable
private fun ProxyValues(
    address: ProxyAddressUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        StepTitle(text = stringResource(Res.string.proxy_wifi_values))
        CopyableValue(label = stringResource(Res.string.proxy_server), value = address.ip)
        CopyableValue(label = stringResource(Res.string.proxy_port), value = address.proxyPort)
        StepTitle(
            text = stringResource(Res.string.proxy_certificate_page),
            modifier = Modifier.padding(top = FloconTheme.spacing.medium),
        )
        QrCodeImage(
            content = address.caUrl,
            modifier = Modifier
                .size(QrSize)
                .clip(FloconTheme.shapes.large),
        )
        CopyableValue(label = null, value = address.caUrl)
    }
}

private enum class PhonePlatform { Android, Ios }

/** The steps stay here, on the desktop: the phone only visits the certificate page for a few seconds. */
@Composable
private fun PlatformSteps(
    modifier: Modifier = Modifier,
) {
    var platform by remember { mutableStateOf(PhonePlatform.Android) }
    val steps = when (platform) {
        PhonePlatform.Android -> listOf(
            Res.string.proxy_android_step_wifi,
            Res.string.proxy_android_step_download,
            Res.string.proxy_android_step_install,
            Res.string.proxy_android_step_app,
        )

        PhonePlatform.Ios -> listOf(
            Res.string.proxy_ios_step_wifi,
            Res.string.proxy_ios_step_download,
            Res.string.proxy_ios_step_install,
            Res.string.proxy_ios_step_trust,
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        FloconSegmentedTextToggle(
            options = persistentListOf(
                stringResource(Res.string.proxy_platform_android),
                stringResource(Res.string.proxy_platform_ios),
            ),
            selectedIndex = platform.ordinal,
            onSelect = { platform = PhonePlatform.entries[it] },
        )
        steps.forEachIndexed { index, step ->
            NumberedStep(number = index + 1, text = stringResource(step))
        }
        if (platform == PhonePlatform.Android) {
            Text(
                text = USER_CERTIFICATES_SNIPPET,
                style = FloconTheme.typography.monoMedium,
                color = FloconTheme.colors.ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FloconTheme.colors.bg100, FloconTheme.shapes.medium)
                    .padding(FloconTheme.spacing.small),
            )
        }
        StepText(text = stringResource(Res.string.proxy_once_hint))
    }
}

@Composable
private fun NumberedStep(
    number: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = number.toString(),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.accentInk,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(StepNumberSize)
                .background(FloconTheme.colors.accent, CircleShape),
        )
        Text(
            text = text,
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
        )
    }
}

@Composable
private fun StepTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FloconTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = FloconTheme.colors.accent,
        modifier = modifier,
    )
}

@Composable
private fun StepText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.inkMuted,
        modifier = modifier,
    )
}

/** A value to type on the phone, with a button to copy it (to send it to the phone, for instance). */
@Composable
private fun CopyableValue(
    label: String?,
    value: String,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
                textAlign = TextAlign.End,
                modifier = Modifier.width(LabelWidth),
            )
        }
        Text(
            text = value,
            style = FloconTheme.typography.monoMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
        )
        FloconIconButton(
            imageVector = Icons.Outlined.ContentCopy,
            onClick = { clipboardManager.setText(AnnotatedString(value)) },
            tooltip = stringResource(Res.string.proxy_copy),
        )
    }
}

@Preview
@Composable
private fun ProxyDialogRunningPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun ProxyDialogOffPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(status = ProxyStatusUiModel.Off),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun ProxyDialogBinaryNotFoundPreview() {
    FloconTheme {
        ProxyDialog(
            uiState = previewProxyUiState(status = ProxyStatusUiModel.BinaryNotFound),
            onEnabledChange = {},
            onDismissRequest = {},
        )
    }
}
