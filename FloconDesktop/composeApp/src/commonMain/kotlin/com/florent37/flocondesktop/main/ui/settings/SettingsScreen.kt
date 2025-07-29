package com.florent37.flocondesktop.main.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.florent37.flocondesktop.common.ui.FloconColorScheme
import com.florent37.flocondesktop.common.ui.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = koinViewModel()

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
        )
        if (displayAboutScreen) {
            Dialog(
                onDismissRequest = {
                    viewModel.hideAboutScreen()
                }
            ) {
                AboutScreen(
                    modifier = Modifier.fillMaxSize()
                        .background(FloconColorScheme.background),
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
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        // Use background color
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth(0.8f) // Take 80% width for content
                    .align(Alignment.Center) // Center the column
                    .background(
                        color = MaterialTheme.colorScheme.surface, // Use surface for the settings panel background
                        shape = RoundedCornerShape(16.dp), // Rounded corners for the panel
                    ).padding(24.dp),
                // Generous padding inside the panel
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp), // Consistent spacing between elements
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium, // Larger title for prominence
                    color = MaterialTheme.colorScheme.onSurface, // Text color on surface
                    modifier = Modifier.padding(bottom = 16.dp), // More space below title
                )

                // Row for Save and Test ADB buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End, // Align buttons to the end
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = adbPathText,
                        onValueChange = { onAdbPathChanged(it) },
                        label = { Text("ADB FilePathDomainModel") }, // Changed label to English for consistency with code
                        placeholder = {
                            Text(
                                "Ex: /Users/youruser/Library/Android/sdk/platform-tools/adb",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // Themed placeholder
                            )
                        },
                        modifier = Modifier.weight(1f), // Fill width of the settings panel
                        singleLine = true,
                        // textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface), // Themed text style
                        // labelTextStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)), // Themed label style
                        // colors = TextFieldDefaults.outlinedTextFieldColors(
                        //    focusedBorderColor = MaterialTheme.colorScheme.primary,
                        //    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        //    cursorColor = MaterialTheme.colorScheme.primary,
                        //    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Slightly different background for text field
                        //    focusedLabelColor = MaterialTheme.colorScheme.primary,
                        //    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        // )
                    )
                    Button(
                        onClick = { saveAdbPath() },
                        colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Use primary for save button
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        shape = RoundedCornerShape(8.dp), // Rounded button corners
                    ) {
                        Text("Save")
                    }
                }

                // Row for Save and Test ADB buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End, // Align buttons to the end
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { testAdbPath() },
                        shape = RoundedCornerShape(8.dp), // Rounded button corners
                        modifier = Modifier.padding(end = 8.dp), // Spacing between buttons
                    ) {
                        Text("Test ADB")
                    }
                }

                Button(
                    onClick = { onClickLicenses() },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(8.dp), // Rounded button corners
                ) {
                    Text("Licenses")
                }
            }


        }
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
            onClickLicenses = {},
        )
    }
}
