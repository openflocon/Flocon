package io.github.openflocon.flocondesktop.features.connect.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.connect_empty
import flocondesktop.composeapp.generated.resources.connect_hide_qr
import flocondesktop.composeapp.generated.resources.connect_hint
import flocondesktop.composeapp.generated.resources.connect_show_qr
import flocondesktop.composeapp.generated.resources.connect_title
import io.github.openflocon.flocondesktop.features.connect.ConnectViewModel
import io.github.openflocon.flocondesktop.features.connect.model.ConnectUiState
import io.github.openflocon.flocondesktop.features.connect.model.LocalAddressUiModel
import io.github.openflocon.flocondesktop.features.connect.model.previewConnectUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
    var qrIp by remember { mutableStateOf<String?>(null) }

    FloconDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.width(480.dp),
    ) {
        Column {
            FloconDialogHeader(
                title = stringResource(Res.string.connect_title),
                trailingContent = {
                    FloconIconButton(onClick = onDismissRequest) {
                        FloconIcon(Icons.Outlined.Close)
                    }
                },
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = FloconTheme.spacing.medium)
                    .padding(bottom = FloconTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(Res.string.connect_hint),
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkMuted,
                )
                if (uiState.addresses.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.connect_empty),
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colors.ink,
                    )
                }
                uiState.addresses.forEach { address ->
                    AddressRow(
                        address = address,
                        qrVisible = qrIp == address.ip,
                        onToggleQr = { qrIp = if (qrIp == address.ip) null else address.ip },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressRow(
    address: LocalAddressUiModel,
    qrVisible: Boolean,
    onToggleQr: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.ip,
                    style = FloconTheme.typography.monoMedium,
                    color = FloconTheme.colors.ink,
                )
                Text(
                    text = address.interfaceName,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkMuted,
                )
            }
            Spacer(Modifier.width(FloconTheme.spacing.small))
            if (qrVisible) {
                FloconOutlinedButton(onClick = onToggleQr) {
                    Text(stringResource(Res.string.connect_hide_qr))
                }
            } else {
                FloconButton(onClick = onToggleQr) {
                    Text(stringResource(Res.string.connect_show_qr))
                }
            }
        }
        if (qrVisible) {
            // the SDK connects with the bare ip, see Flocon.connect()
            QrCodeImage(
                content = address.ip,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(220.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ConnectDialogPreview() {
    FloconTheme {
        ConnectDialog(
            uiState = previewConnectUiState(),
            onDismissRequest = {},
        )
    }
}
