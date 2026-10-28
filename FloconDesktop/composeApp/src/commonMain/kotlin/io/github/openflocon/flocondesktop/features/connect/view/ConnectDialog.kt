package io.github.openflocon.flocondesktop.features.connect.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import flocondesktop.composeapp.generated.resources.connect_copy
import flocondesktop.composeapp.generated.resources.connect_empty
import flocondesktop.composeapp.generated.resources.connect_hint
import flocondesktop.composeapp.generated.resources.connect_pick_network
import flocondesktop.composeapp.generated.resources.connect_port
import flocondesktop.composeapp.generated.resources.connect_scan
import flocondesktop.composeapp.generated.resources.connect_title
import io.github.openflocon.flocondesktop.features.connect.ConnectViewModel
import io.github.openflocon.flocondesktop.features.connect.model.ConnectUiState
import io.github.openflocon.flocondesktop.features.connect.model.LocalAddressUiModel
import io.github.openflocon.flocondesktop.features.connect.model.previewConnectUiState
import io.github.openflocon.flocondesktop.features.connect.model.previewSingleConnectUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val SingleWidth = 420.dp
private val MultiWidth = 680.dp
private val QrSize = 216.dp

@Composable
fun ConnectDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ConnectViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) { viewModel.refresh() }

    ConnectDialog(
        uiState = uiState,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}

@Composable
private fun ConnectDialog(
    uiState: ConnectUiState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val multiple = uiState.addresses.size > 1

    FloconDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.width(if (multiple) MultiWidth else SingleWidth),
    ) {
        // FloconDialog has no border, drawn here over the content so that the window stands out
        Box(modifier = Modifier.border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(10.dp))) {
            if (multiple) {
                MultipleAddresses(
                    uiState = uiState,
                    onClose = onDismissRequest,
                )
            } else {
                SingleAddress(
                    address = uiState.addresses.firstOrNull(),
                    port = uiState.port,
                    onClose = onDismissRequest,
                )
            }
        }
    }
}

@Composable
private fun SingleAddress(
    address: LocalAddressUiModel?,
    port: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colors.bg000)
                .padding(start = FloconTheme.spacing.extraLarge, end = FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.connect_title),
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
                modifier = Modifier.weight(1f),
            )
            FloconIconButton(onClick = onClose, modifier = Modifier.padding(vertical = FloconTheme.spacing.small)) {
                FloconIcon(Icons.Outlined.Close)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(FloconTheme.colors.line))
        if (address == null) {
            Text(
                text = stringResource(Res.string.connect_empty),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.inkMuted,
                modifier = Modifier.padding(FloconTheme.spacing.extraLarge),
            )
        } else {
            ConnectMascotScene()
            AddressPreview(
                address = address,
                port = port,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FloconTheme.spacing.extraLarge),
            )
        }
    }
}

@Composable
private fun MultipleAddresses(
    uiState: ConnectUiState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIp by remember { mutableStateOf<String?>(null) }
    val selected = uiState.addresses.firstOrNull { it.ip == selectedIp } ?: uiState.addresses.first()

    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .background(FloconTheme.colors.bg000)
                .verticalScroll(rememberScrollState())
                .padding(FloconTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            Text(
                text = stringResource(Res.string.connect_title),
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
            )
            Text(
                text = stringResource(Res.string.connect_pick_network),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkMuted,
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.small).padding(bottom = FloconTheme.spacing.small),
            )
            uiState.addresses.forEach { address ->
                AddressRow(
                    address = address,
                    selected = address.ip == selected.ip,
                    onClick = { selectedIp = address.ip },
                )
            }
        }
        Box(Modifier.fillMaxHeight().width(1.dp).background(FloconTheme.colors.line))
        Box(modifier = Modifier.weight(1f)) {
            Column {
                ConnectMascotScene()
                AddressPreview(
                    address = selected,
                    port = uiState.port,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(FloconTheme.spacing.extraLarge),
                )
            }
            FloconIconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(FloconTheme.spacing.small),
            ) {
                FloconIcon(Icons.Outlined.Close)
            }
        }
    }
}

@Composable
private fun AddressRow(
    address: LocalAddressUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) colors.accentSoft else Color.Transparent)
            .border(1.dp, if (selected) colors.accent.copy(alpha = 0.4f) else Color.Transparent, shape)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (selected) colors.accent else colors.lineStrong),
        )
        Column {
            Text(
                text = address.ip,
                style = FloconTheme.typography.monoMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            Text(
                text = address.interfaceName,
                style = FloconTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
        }
    }
}

@Composable
private fun AddressPreview(
    address: LocalAddressUiModel,
    port: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        Text(
            text = stringResource(Res.string.connect_scan).uppercase(),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.accent,
        )
        // the SDK connects with the bare ip, see Flocon.connect()
        QrCodeImage(
            content = address.ip,
            modifier = Modifier
                .size(QrSize)
                .clip(FloconTheme.shapes.large),
        )
        Text(
            text = address.ip,
            style = FloconTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = "${address.interfaceName} · ${stringResource(Res.string.connect_port, port)}",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
        CopyButton(ip = address.ip)
        Text(
            text = stringResource(Res.string.connect_hint, address.ip),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CopyButton(
    ip: String,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Row(
        modifier = modifier
            .scale(scale.value)
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.accent)
            .clickable(interactionSource = null, indication = null) {
                clipboardManager.setText(AnnotatedString(ip))
                scope.launch {
                    scale.animateTo(0.88f, tween(durationMillis = 70))
                    scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium))
                }
            }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconIcon(
            imageVector = Icons.Outlined.ContentCopy,
            tint = FloconTheme.colors.accentInk,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(Res.string.connect_copy),
            style = FloconTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.accentInk,
        )
    }
}

@Preview
@Composable
private fun ConnectDialogMultiplePreview() {
    FloconTheme {
        ConnectDialog(
            uiState = previewConnectUiState(),
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun ConnectDialogSinglePreview() {
    FloconTheme {
        ConnectDialog(
            uiState = previewSingleConnectUiState(),
            onDismissRequest = {},
        )
    }
}
