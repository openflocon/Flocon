package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConnectResult
import io.github.openflocon.flocon.FloconConnectionState
import io.github.openflocon.flocon.connect
import io.github.openflocon.flocon.connectionState
import io.github.openflocon.flocon.myapplication.multi.ui.components.AccentButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.components.ScreenScaffold
import io.github.openflocon.flocon.myapplication.multi.ui.components.SectionLabel
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleCategory
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleIcons
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme
import kotlinx.coroutines.launch

/**
 * Connects the app to Flocon Desktop at an IP address (a real device cannot reach the computer through
 * localhost), with `Flocon.connect`. The port is always 9023.
 */
@Composable
fun ConnectionScreen(onBack: () -> Unit) {
    val colors = SampleTheme.colors
    val state by Flocon.connectionState.collectAsState()
    val scope = rememberCoroutineScope()
    var address by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<FloconConnectResult?>(null) }
    var connecting by remember { mutableStateOf(false) }

    val connect: () -> Unit = {
        if (address.isNotBlank() && !connecting) {
            scope.launch {
                connecting = true
                result = Flocon.connect(address.trim())
                connecting = false
            }
        }
    }

    ScreenScaffold(
        title = "Remote connection",
        subtitle = "Connect to Flocon Desktop by IP",
        category = SampleCategory.Neutral,
        icon = SampleIcons.Connection,
        onBack = onBack,
    ) {
        Column {
            SectionLabel("Status")
            Card {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Spacer(Modifier.size(10.dp).clip(CircleShape).background(state.color()))
                    Text(
                        state.label(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.ink,
                    )
                }
            }
        }

        Column {
            SectionLabel("Flocon Desktop address")
            Card {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceHigh)
                            .border(BorderStroke(1.dp, colors.line), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                    ) {
                        if (address.isEmpty()) {
                            Text("192.168.1.20", style = MaterialTheme.typography.bodyLarge, color = colors.inkFaint)
                        }
                        BasicTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.ink),
                            cursorBrush = SolidColor(colors.accent),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = { connect() }),
                        )
                    }
                    AccentButton(
                        text = if (connecting) "…" else "Connect",
                        enabled = address.isNotBlank() && !connecting,
                        onClick = connect,
                    )
                }
                when (val current = result) {
                    is FloconConnectResult.Failed -> ResultLine("✕ ${current.reason}", colors.error)
                    is FloconConnectResult.InvalidAddress -> ResultLine("✕ ${current.reason}", colors.error)
                    FloconConnectResult.NotStarted -> ResultLine("✕ Flocon is not started", colors.error)
                    FloconConnectResult.Connected -> ResultLine("✓ Connected", colors.success)
                    null -> Unit
                }
            }
        }

        InfoCard(
            text = "Flocon keeps trying its default address (localhost on an emulator) and also connects to this " +
                "one. The port is always 9023.",
            tint = colors.accent,
        )
    }
}

@Composable
private fun ResultLine(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
    )
}

/** The button of the home, next to the platform: the dot shows the state of the connection. */
@Composable
fun ConnectionChip(onClick: () -> Unit) {
    val colors = SampleTheme.colors
    val state by Flocon.connectionState.collectAsState()
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.surfaceHigh)
            .border(1.dp, colors.line, CircleShape)
            .clickable(role = Role.Button, onClickLabel = "Remote connection", onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(SampleIcons.Connection, contentDescription = null, tint = colors.ink, modifier = Modifier.size(14.dp))
        Text("Connect", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = colors.ink)
        Spacer(Modifier.size(8.dp).clip(CircleShape).background(state.color()))
    }
}

@Composable
private fun FloconConnectionState.color(): Color = with(SampleTheme.colors) {
    when (this@color) {
        FloconConnectionState.Connected -> success
        FloconConnectionState.Connecting -> warning
        is FloconConnectionState.Failed -> error
        FloconConnectionState.Idle, FloconConnectionState.Buffering -> inkFaint
    }
}

private fun FloconConnectionState.label() = when (this) {
    FloconConnectionState.Idle -> "Flocon is not started"
    FloconConnectionState.Buffering -> "Not connected, messages are kept"
    FloconConnectionState.Connecting -> "Connecting…"
    FloconConnectionState.Connected -> "Connected to Flocon Desktop"
    is FloconConnectionState.Failed -> "Failed: $reason"
}
