package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_save
import flocondesktop.composeapp.generated.resources.settings_adb_setup_title
import flocondesktop.composeapp.generated.resources.settings_adb_valid
import flocondesktop.composeapp.generated.resources.settings_font_size_multiplier
import flocondesktop.composeapp.generated.resources.settings_test
import io.github.openflocon.flocondesktop.common.log.LogEntryUiModel
import io.github.openflocon.flocondesktop.common.log.LogLevel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSlider
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

// ---------------------------------------------------------------------------
// Tabs
// ---------------------------------------------------------------------------

private enum class SettingsTab(val label: String, val icon: ImageVector) {
    Adb("Adb", Icons.Outlined.Cable),
    Appearance("Appearance", Icons.Outlined.TextFields),
    Logs("Logs", Icons.Outlined.List),
    About("About", Icons.Outlined.Info),
}

// ---------------------------------------------------------------------------
// Entry point
// ---------------------------------------------------------------------------

@Composable
fun SettingsScreen(
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
        onClearLogs = viewModel::clearLogs,
        needsAdbSetup = needsAdbSetup,
    )
}

// ---------------------------------------------------------------------------
// Main layout — Permanent drawer + content pane
// ---------------------------------------------------------------------------

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    adbPathText: String,
    onAdbPathChanged: (String) -> Unit,
    saveAdbPath: () -> Unit,
    testAdbPath: () -> Unit,
    needsAdbSetup: Boolean,
    onAction: (SettingsAction) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(SettingsTab.Adb) }

    Row(modifier = modifier) {
        // ── Drawer ──────────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            SettingsTab.entries.forEach { tab ->
                DrawerItem(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { selectedTab = tab },
                )
            }
        }

        FloconVerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            color = FloconTheme.colorPalette.secondary,
        )

        // ── Content pane ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                SettingsTab.Adb -> AdbPane(
                    adbPathText = adbPathText,
                    onAdbPathChanged = onAdbPathChanged,
                    saveAdbPath = saveAdbPath,
                    testAdbPath = testAdbPath,
                    needsAdbSetup = needsAdbSetup,
                )

                SettingsTab.Appearance -> AppearancePane(
                    fontSizeMultiplier = uiState.fontSizeMultiplier,
                    onAction = onAction,
                )

                SettingsTab.Logs -> LogsPane(
                    logs = uiState.logs,
                    onClearLogs = onClearLogs,
                )

                SettingsTab.About -> AboutPane()
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Drawer item
// ---------------------------------------------------------------------------

@Composable
private fun DrawerItem(
    tab: SettingsTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) FloconTheme.colorPalette.secondary else FloconTheme.colorPalette.primary
    val contentColor = if (selected) FloconTheme.colorPalette.onSecondary else FloconTheme.colorPalette.onPrimary

    FloconSurface(
        onClick = onClick,
        color = bgColor,
        contentColor = contentColor,
        shape = FloconTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            FloconIcon(
                imageVector = tab.icon,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = tab.label,
                style = FloconTheme.typography.bodyMedium,
                color = contentColor,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Content panes
// ---------------------------------------------------------------------------

@Composable
private fun AdbPane(
    adbPathText: String,
    onAdbPathChanged: (String) -> Unit,
    saveAdbPath: () -> Unit,
    testAdbPath: () -> Unit,
    needsAdbSetup: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Status row
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
                    style = FloconTheme.typography.bodySmall,
                )
            }
        }
        FloconSection(
            title = stringResource(Res.string.settings_theme),
            initialValue = true
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
                    .padding(all = 8.dp)
            ) {
                ThemeSetting.entries.forEach { theme ->
                    ThemeButton(
                        theme = theme,
                        selected = uiState.theme == theme,
                        onClick = { onAction(SettingsAction.ThemeChange(theme)) },
                    )
                }
            }
        }
        FloconSection(
            title = stringResource(Res.string.settings_about_title),
            initialValue = true
        ) {
            SettingsButton(
                text = stringResource(Res.string.general_save),
                onClick = saveAdbPath,
            )
            SettingsButton(
                text = stringResource(Res.string.settings_test),
                onClick = testAdbPath,
            )
        }
    }
}

@Composable
private fun AppearancePane(
    fontSizeMultiplier: Float,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.settings_font_size_multiplier, fontSizeMultiplier),
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colorPalette.onPrimary,
        )
        FloconSlider(
            value = fontSizeMultiplier,
            onValueChange = { onAction(SettingsAction.FontSizeMultiplierChange(it)) },
            valueRange = 1f..2f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LogsPane(
    logs: List<LogEntryUiModel>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Console (${logs.size})",
                style = FloconTheme.typography.titleMedium,
                color = FloconTheme.colorPalette.onPrimary,
            )
            if (logs.isNotEmpty()) {
                SettingsButton(
                    text = "Clear",
                    onClick = onClearLogs,
                )
            }
        }

        if (logs.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "No logs yet",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
                )
            }
        } else {
            ConsoleLogPanel(
                logs = logs,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }
    }
}

@Composable
private fun AboutPane(
    modifier: Modifier = Modifier,
) {
    AboutScreen(
        modifier = modifier
            .fillMaxSize()
            .background(FloconTheme.colorPalette.primary),
    )
}

// ---------------------------------------------------------------------------
// Shared helpers
// ---------------------------------------------------------------------------

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

@Composable
private fun ThemeButton(
    theme: ThemeSetting,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconButton(
        onClick = onClick,
        containerColor = if (selected) {
            FloconTheme.colorPalette.accent
        } else {
            FloconTheme.colorPalette.secondary
        },
        modifier = modifier
    ) {
        Text(
            text = stringResource(
                when (theme) {
                    ThemeSetting.Dark -> Res.string.settings_theme_dark
                    ThemeSetting.Light -> Res.string.settings_theme_light
                    ThemeSetting.System -> Res.string.settings_theme_system
                }
            ),
            style = FloconTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LicensesWindow(
    onCloseRequest: () -> Unit
private fun ConsoleLogPanel(
    logs: List<LogEntryUiModel>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.lastIndex)
    }
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.secondary)
            .padding(8.dp)
    ) {
        items(logs) { entry ->
            val color = when (entry.level) {
                LogLevel.ERROR -> FloconTheme.colorPalette.onError
                LogLevel.DEBUG -> FloconTheme.colorPalette.onAccent
            }
            Text(
                text = "[${entry.level.name}] ${entry.message}",
                color = color,
                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview
@Composable
private fun SettingsScreenPreview() {
    FloconTheme {
        var adbPath by remember { mutableStateOf("/usr/local/bin/adb") }
        SettingsScreen(
            uiState = previewSettingsUiState(),
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = { Logger.d { "Save ADB: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB: $adbPath" } },
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
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
            saveAdbPath = { Logger.d { "Save ADB: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB: $adbPath" } },
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
            needsAdbSetup = true,
        )
    }
}

@Preview
@Composable
private fun SettingsScreen_LogsPreview() {
    FloconTheme {
        SettingsScreen(
            uiState = previewSettingsUiState(),
            adbPathText = "/usr/local/bin/adb",
            onAdbPathChanged = {},
            saveAdbPath = {},
            testAdbPath = {},
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
            needsAdbSetup = false,
        )
    }
}
