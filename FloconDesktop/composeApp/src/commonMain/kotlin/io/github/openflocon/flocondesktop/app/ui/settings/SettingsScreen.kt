package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_save
import flocondesktop.composeapp.generated.resources.settings_adb_setup_title
import flocondesktop.composeapp.generated.resources.settings_adb_valid
import flocondesktop.composeapp.generated.resources.settings_font_size_multiplier
import flocondesktop.composeapp.generated.resources.settings_test
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconSlider
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        adbPathText = adbPathText,
        onAdbPathChanged = viewModel::onAdbPathChanged,
        saveAdbPath = viewModel::saveAdbPath,
        testAdbPath = viewModel::testAdbPath,
        onAction = viewModel::onAction,
        needsAdbSetup = needsAdbSetup,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    adbPathText: String,
    onAdbPathChanged: (String) -> Unit,
    saveAdbPath: () -> Unit,
    testAdbPath: () -> Unit,
    needsAdbSetup: Boolean,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        FloconSection(
            title = "Adb Path",
            initialValue = true
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
                    .padding(all = 8.dp)
            ) {
                if (needsAdbSetup) {
                    Text(
                        text = stringResource(Res.string.settings_adb_setup_title),
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
                            text = stringResource(Res.string.settings_adb_valid),
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
                        text = stringResource(Res.string.general_save),
                        onClick = saveAdbPath
                    )
                    SettingsButton(
                        onClick = testAdbPath,
                        text = stringResource(Res.string.settings_test),
                    )
                }
            }
        }
        FloconSection(
            title = stringResource(Res.string.settings_font_size_multiplier, uiState.fontSizeMultiplier),
            initialValue = true
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
                    .padding(all = 8.dp)
            ) {
                FloconSlider(
                    value = uiState.fontSizeMultiplier,
                    onValueChange = { onAction(SettingsAction.FontSizeMultiplierChange(it)) },
                    valueRange = 1f..2f,
                    modifier = Modifier.fillMaxWidth()
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
            uiState = previewSettingsUiState(),
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = {
                Logger.d { "Save ADB FilePathDomainModel: $adbPath" }
            },
            testAdbPath = {
                Logger.d { "Test ADB FilePathDomainModel: $adbPath" }
            },
            modifier = Modifier.fillMaxSize(),
            onAction = {},
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
            uiState = previewSettingsUiState(),
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = { Logger.d { "Save ADB FilePathDomainModel: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB FilePathDomainModel: $adbPath" } },
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            needsAdbSetup = true,
        )
    }
}
