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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.proxy_binary_not_found
import flocondesktop.composeapp.generated.resources.proxy_ca_android
import flocondesktop.composeapp.generated.resources.proxy_ca_ios
import flocondesktop.composeapp.generated.resources.proxy_copy
import flocondesktop.composeapp.generated.resources.proxy_devices_hint
import flocondesktop.composeapp.generated.resources.proxy_experimental
import flocondesktop.composeapp.generated.resources.proxy_failed
import flocondesktop.composeapp.generated.resources.proxy_intro
import flocondesktop.composeapp.generated.resources.proxy_no_address
import flocondesktop.composeapp.generated.resources.proxy_off
import flocondesktop.composeapp.generated.resources.proxy_pick_network
import flocondesktop.composeapp.generated.resources.proxy_port
import flocondesktop.composeapp.generated.resources.proxy_server
import flocondesktop.composeapp.generated.resources.proxy_starting
import flocondesktop.composeapp.generated.resources.proxy_step_ca
import flocondesktop.composeapp.generated.resources.proxy_step_ca_hint
import flocondesktop.composeapp.generated.resources.proxy_step_wifi
import flocondesktop.composeapp.generated.resources.proxy_step_wifi_hint
import flocondesktop.composeapp.generated.resources.proxy_title
import flocondesktop.composeapp.generated.resources.proxy_wifi_android
import flocondesktop.composeapp.generated.resources.proxy_wifi_ios
import io.github.openflocon.flocondesktop.features.connect.view.QrCodeImage
import io.github.openflocon.flocondesktop.features.proxy.ProxyViewModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.previewProxyUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconSwitch
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val DialogWidth = 720.dp
private val QrSize = 180.dp
private val LabelWidth = 56.dp

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
    FloconDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.width(DialogWidth),
    ) {
        // FloconDialog has no border, drawn here so that the window stands out
        Column(modifier = Modifier.border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(10.dp))) {
            Header(
                enabled = uiState.enabled,
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
    onEnabledChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FloconTheme.colors.bg000)
            .padding(start = FloconTheme.spacing.extraLarge, end = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.proxy_title),
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = stringResource(Res.string.proxy_experimental).uppercase(),
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.accent,
            modifier = Modifier
                .background(FloconTheme.colors.accentSoft, FloconTheme.shapes.small)
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
            WifiStep(
                address = selected,
                modifier = Modifier.weight(1f),
            )
            Box(Modifier.fillMaxHeight().width(1.dp).background(FloconTheme.colors.line))
            CertificateStep(
                address = selected,
                modifier = Modifier.weight(1f),
            )
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

@Composable
private fun WifiStep(
    address: ProxyAddressUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        StepTitle(text = stringResource(Res.string.proxy_step_wifi))
        StepText(text = stringResource(Res.string.proxy_step_wifi_hint))
        CopyableValue(label = stringResource(Res.string.proxy_server), value = address.ip)
        CopyableValue(label = stringResource(Res.string.proxy_port), value = address.proxyPort)
        StepText(text = stringResource(Res.string.proxy_wifi_ios))
        StepText(text = stringResource(Res.string.proxy_wifi_android))
    }
}

@Composable
private fun CertificateStep(
    address: ProxyAddressUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        StepTitle(text = stringResource(Res.string.proxy_step_ca))
        StepText(text = stringResource(Res.string.proxy_step_ca_hint))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            QrCodeImage(
                content = address.caUrl,
                modifier = Modifier
                    .size(QrSize)
                    .clip(FloconTheme.shapes.large),
            )
            CopyableValue(label = null, value = address.caUrl)
        }
        StepText(text = stringResource(Res.string.proxy_ca_ios))
        StepText(text = stringResource(Res.string.proxy_ca_android))
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
