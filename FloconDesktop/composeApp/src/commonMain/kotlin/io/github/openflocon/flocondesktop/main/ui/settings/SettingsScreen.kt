package io.github.openflocon.flocondesktop.main.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val needsAdbSetup by viewModel.needsAdbSetup.collectAsState()
    val adbPathText by viewModel.adbPathInput.collectAsState()

    Box(modifier = modifier) {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            adbPathText = adbPathText,
            onAdbPathChanged = viewModel::onAdbPathChanged,
            saveAdbPath = viewModel::saveAdbPath,
            testAdbPath = viewModel::testAdbPath,
            needsAdbSetup = needsAdbSetup,
        )
    }
}

// Main composable for the screen, incorporating the filter bar
@Composable
private fun SettingsScreen(
    adbPathText: String,
    onAdbPathChanged: (String) -> Unit,
    saveAdbPath: () -> Unit,
    testAdbPath: () -> Unit,
    needsAdbSetup: Boolean,
    modifier: Modifier = Modifier,
) {
    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
                .padding(all = 8.dp)
        ) {
            if (needsAdbSetup) {
                Text(
                    text = "Please setup ADB first, this field is mandatory",
                    color = FloconTheme.colorPalette.onError,
                    style = FloconTheme.typography.bodySmall,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colorPalette.onAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "ADB configuraton is valid",
                        color = FloconTheme.colorPalette.onAccent,
                        style = FloconTheme.typography.bodySmall
                    )
                }
            }
            FloconTextFieldWithoutM3(
                value = adbPathText,
                onValueChange = onAdbPathChanged,
                placeholder = defaultPlaceHolder("Eg: /Users/youruser/Library/Android/sdk/platform-tools/adb"),
                containerColor = FloconTheme.colorPalette.secondary,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsButton(
                    text = "Save",
                    onClick = saveAdbPath
                )
                SettingsButton(
                    onClick = testAdbPath,
                    text = "Test",
                )
            }
        }
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    FloconButton(
        onClick = onClick,
        containerColor = FloconTheme.colorPalette.secondary,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    FloconTheme {
        var adbPath by remember { mutableStateOf("/usr/local/bin/adb") }
        SettingsScreen(
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = {
                Logger.d { "Save ADB FilePathDomainModel: $adbPath" }
            },
            testAdbPath = {
                Logger.d { "Test ADB FilePathDomainModel: $adbPath" }
            },
            modifier = Modifier.fillMaxSize(),
            needsAdbSetup = false,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview_needsAdbSetup() {
    FloconTheme {
        var adbPath by remember { mutableStateOf("/usr/local/bin/adb") }
        SettingsScreen(
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = { Logger.d { "Save ADB FilePathDomainModel: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB FilePathDomainModel: $adbPath" } },
            modifier = Modifier.fillMaxSize(),
            needsAdbSetup = true,
        )
    }
}
