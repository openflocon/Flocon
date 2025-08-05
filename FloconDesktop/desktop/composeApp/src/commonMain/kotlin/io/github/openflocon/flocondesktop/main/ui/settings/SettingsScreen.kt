package io.github.openflocon.flocondesktop.main.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = koinViewModel()

    val needsAdbSetup by viewModel.needsAdbSetup.collectAsState()
    val adbPathText by viewModel.adbPathInput.collectAsState()
    val displayAboutScreen by viewModel.displayAboutScreen.collectAsState()

    Box(modifier = modifier) {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            adbPathText = adbPathText,
            onAdbPathChanged = viewModel::onAdbPathChanged,
            saveAdbPath = viewModel::saveAdbPath,
            testAdbPath = viewModel::testAdbPath,
            onClickLicenses = viewModel::displayAboutScreen,
            needsAdbSetup = needsAdbSetup,
        )
        if (displayAboutScreen) {
            Dialog(
                onDismissRequest = {
                    viewModel.hideAboutScreen()
                },
            ) {
                AboutScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FloconTheme.colorPalette.background),
                )
            }
        }
    }
}

// Main composable for the screen, incorporating the filter bar
@Composable
private fun SettingsScreen(
    adbPathText: String,
    onAdbPathChanged: (String) -> Unit,
    saveAdbPath: () -> Unit,
    testAdbPath: () -> Unit,
    onClickLicenses: () -> Unit,
    needsAdbSetup: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(FloconTheme.colorPalette.panel)
                    .padding(all = 12.dp),
            ) {
                Text(
                    text = "Settings",
                    style = FloconTheme.typography.titleLarge,
                    color = FloconTheme.colorPalette.onSurface,
                )

                SettingsButton(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onClickLicenses,
                    text = "Licenses",
                )
            }

            Column(modifier = Modifier.fillMaxSize().padding(all = 16.dp)) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(FloconTheme.colorPalette.panel)
                        .padding(all = 12.dp),
                ) {
                    if (needsAdbSetup) {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .padding(horizontal = 8.dp),
                            text = "Please setup ADB first, this field is mandatory",
                            color = Color.Red,
                            style = FloconTheme.typography.bodySmall,
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .padding(horizontal = 8.dp),
                            text = "ADB configuraton is valid",
                            color = Color.Green,
                            style = FloconTheme.typography.bodySmall,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            if (adbPathText.isEmpty())
                                Text(
                                    text = "Ex: /Users/youruser/Library/Android/sdk/platform-tools/adb",
                                    style = FloconTheme.typography.bodySmall,
                                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                                )
                            BasicTextField(
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = FloconTheme.typography.bodySmall.copy(
                                    color = FloconTheme.colorPalette.onSurface,
                                ),
                                value = adbPathText,
                                onValueChange = { onAdbPathChanged(it) },
                                cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                            )
                        }
                        SettingsButton(
                            text = "Save",
                            onClick = {
                                saveAdbPath()
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End, // Align buttons to the end
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SettingsButton(
                            onClick = { testAdbPath() },
                            text = "Test ADB",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    text: String,
    shape: Shape = RoundedCornerShape(4.dp),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall.copy(
                color = Color.Black,
            ),
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
            saveAdbPath = { println("Save ADB FilePathDomainModel: $adbPath") },
            testAdbPath = { println("Test ADB FilePathDomainModel: $adbPath") },
            modifier = Modifier.fillMaxSize(),
            needsAdbSetup = false,
            onClickLicenses = {},
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
            saveAdbPath = { println("Save ADB FilePathDomainModel: $adbPath") },
            testAdbPath = { println("Test ADB FilePathDomainModel: $adbPath") },
            modifier = Modifier.fillMaxSize(),
            needsAdbSetup = true,
            onClickLicenses = {},
        )
    }
}
