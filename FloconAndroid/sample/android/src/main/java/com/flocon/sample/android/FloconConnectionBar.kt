package com.flocon.sample.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult
import io.github.openflocon.flocon.FloconConnectionState
import io.github.openflocon.flocon.connect
import io.github.openflocon.flocon.connectionState
import kotlinx.coroutines.launch

/**
 * Shows the state of the connection to the Flocon desktop app, and lets you type its IP address
 * (for a real device, where localhost does not reach your computer). The port is optional: 9023 is used.
 */
@Composable
fun FloconConnectionBar(modifier: Modifier = Modifier) {
    val state by Flocon.connectionState.collectAsState()
    val scope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<FloconConnectResult?>(null) }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Flocon: ${state.label()}",
            style = MaterialTheme.typography.titleSmall,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Flocon IP (port 9023 by default)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
            Button(
                enabled = address.isNotBlank(),
                onClick = {
                    scope.launch { result = Flocon.connect(address) }
                },
            ) {
                Text("Connect")
            }
        }
        (result as? FloconConnectResult.Failed)?.let { Text("Failed: ${it.reason}") }
        (result as? FloconConnectResult.InvalidAddress)?.let { Text("Invalid address: ${it.reason}") }
    }
}

private fun FloconConnectionState.label() = when (this) {
    FloconConnectionState.Idle -> "not started"
    FloconConnectionState.Buffering -> "not connected, messages are kept"
    FloconConnectionState.Connecting -> "connecting..."
    FloconConnectionState.Connected -> "connected"
    is FloconConnectionState.Failed -> "failed ($reason)"
}
