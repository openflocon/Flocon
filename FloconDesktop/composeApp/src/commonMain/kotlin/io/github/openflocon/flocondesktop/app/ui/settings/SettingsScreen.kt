package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_save
import flocondesktop.composeapp.generated.resources.settings_adb_setup_title
import flocondesktop.composeapp.generated.resources.settings_adb_valid
import flocondesktop.composeapp.generated.resources.settings_test
import flocondesktop.composeapp.generated.resources.settings_theme
import flocondesktop.composeapp.generated.resources.settings_theme_dark
import flocondesktop.composeapp.generated.resources.settings_theme_light
import flocondesktop.composeapp.generated.resources.settings_theme_system
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    adbForwardStatus = uiState.adbForwardStatus,
                )

                SettingsTab.Appearance -> AppearancePane(
                    fontSizeMultiplier = uiState.fontSizeMultiplier,
                    currentTheme = uiState.theme,
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
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = FloconTheme.shapes.medium

    val bgColor = when {
        selected -> FloconTheme.colorPalette.secondary
        hovered -> FloconTheme.colorPalette.secondary.copy(alpha = 0.5f)
        else -> FloconTheme.colorPalette.primary.copy(alpha = 0f)
    }

    val contentColor = when {
        selected -> FloconTheme.colorPalette.onSecondary
        hovered -> FloconTheme.colorPalette.onSecondary.copy(alpha = 0.8f)
        else -> FloconTheme.colorPalette.onPrimary
    }

    val borderColor = if (selected) {
        FloconTheme.colorPalette.accent
    } else {
        Color.Transparent
    }

    FloconSurface(
        onClick = onClick,
        color = bgColor,
        contentColor = contentColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            FloconIcon(
                imageVector = tab.icon,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = tab.label,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable Settings Card
// ---------------------------------------------------------------------------

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    description: String? = null,
    headerActions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    FloconSurface(
        color = FloconTheme.colorPalette.primary,
        shape = FloconTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloconIcon(
                    imageVector = icon,
                    tint = FloconTheme.colorPalette.onAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier.weight(1f)
                )
                headerActions?.invoke(this)
            }

            if (description != null) {
                Text(
                    text = description,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
                )
            }

            content()
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
    adbForwardStatus: AdbForwardStatus,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        SettingsCard(
            title = "ADB Configuration",
            icon = Icons.Outlined.Settings,
            description = "Flocon communicates with Android devices using the Android Debug Bridge (ADB). Set the path to your adb binary below."
        ) {
            // Setup alert or status
            if (needsAdbSetup) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colorPalette.error.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Warning,
                        tint = FloconTheme.colorPalette.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(Res.string.settings_adb_setup_title),
                        color = FloconTheme.colorPalette.error,
                        style = FloconTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colorPalette.accent.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colorPalette.onAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(Res.string.settings_adb_valid),
                        color = FloconTheme.colorPalette.onAccent,
                        style = FloconTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "ADB Executable Path",
                style = FloconTheme.typography.labelSmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f)
            )

            FloconTextFieldWithoutM3(
                value = adbPathText,
                onValueChange = onAdbPathChanged,
                placeholder = defaultPlaceHolder("Eg: /Users/youruser/Library/Android/sdk/platform-tools/adb"),
                containerColor = FloconTheme.colorPalette.secondary,
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

        SettingsCard(
            title = "ADB Reverse Port Forwarding",
            icon = Icons.Outlined.Cable,
            description = "Flocon runs a local server that communicates with the daemon on the device. Reverse port forwarding enables high-throughput data transfer (logs, preferences, screenshots)."
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.small)
                    .background(FloconTheme.colorPalette.secondary)
                    .padding(12.dp)
            ) {
                AdbForwardStatusBadge(status = adbForwardStatus)

                Text(
                    text = when (adbForwardStatus) {
                        AdbForwardStatus.OK -> "Reverse port forwarding is active and healthy."
                        AdbForwardStatus.NOK -> "Connection failed. Please ensure ADB is configured correctly and your device is connected."
                        AdbForwardStatus.UNKNOWN -> "Status unknown. Waiting for device or forwarding loop to initialize."
                    },
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.8f),
                    style = FloconTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = when (adbForwardStatus) {
                        AdbForwardStatus.OK -> "Reverse port forwarding is active and healthy."
                        AdbForwardStatus.NOK -> "Connection failed. Please ensure ADB is configured correctly and your device is connected."
                        AdbForwardStatus.UNKNOWN -> "Status unknown. Waiting for device or forwarding loop to initialize."
                    },
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.8f),
                    style = FloconTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class BadgeTheme(
    val label: String,
    val bgColor: Color,
    val textColor: Color,
    val icon: ImageVector
)

@Composable
private fun AdbForwardStatusBadge(
    status: AdbForwardStatus,
    modifier: Modifier = Modifier,
) {
    val theme = when (status) {
        AdbForwardStatus.OK -> BadgeTheme(
            "ACTIVE",
            FloconTheme.colorPalette.accent.copy(alpha = 0.2f),
            FloconTheme.colorPalette.onAccent,
            Icons.Outlined.Check
        )

        AdbForwardStatus.NOK -> BadgeTheme(
            "FAILED",
            FloconTheme.colorPalette.error.copy(alpha = 0.2f),
            FloconTheme.colorPalette.error,
            Icons.Outlined.ErrorOutline
        )

        AdbForwardStatus.UNKNOWN -> BadgeTheme(
            "PENDING",
            FloconTheme.colorPalette.secondary.copy(alpha = 0.5f),
            FloconTheme.colorPalette.onSecondary,
            Icons.Outlined.Info
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(theme.bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        FloconIcon(
            imageVector = theme.icon,
            tint = theme.textColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = theme.label,
            color = theme.textColor,
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AppearancePane(
    fontSizeMultiplier: Float,
    currentTheme: ThemeSetting,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        SettingsCard(
            title = stringResource(Res.string.settings_theme),
            icon = Icons.Outlined.ModeNight,
            description = "Choose the color scheme of Flocon. Light mode uses bright backgrounds, Dark mode uses dark backgrounds, and System automatically matches your operating system."
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ThemeSetting.entries.forEach { theme ->
                    ThemeButton(
                        theme = theme,
                        selected = currentTheme == theme,
                        onClick = { onAction(SettingsAction.ThemeChange(theme)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        SettingsCard(
            title = "Text Scaling",
            icon = Icons.Outlined.TextFields,
            description = "Increase or decrease the font size multiplier to scale all application labels, logs, and values."
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FloconSlider(
                    value = fontSizeMultiplier,
                    onValueChange = { onAction(SettingsAction.FontSizeMultiplierChange(it)) },
                    valueRange = 1f..2f,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${(fontSizeMultiplier * 100).toInt()}%",
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier.width(48.dp)
                )
            }
        }
    }
}

@Composable
private fun LogsPane(
    logs: List<LogEntryUiModel>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Flocon System Logs",
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier.weight(1f)
            )

            if (logs.isNotEmpty()) {
                FloconButton(
                    onClick = {
                        val text = logs.joinToString("\n") { "[${it.timestamp} ${it.level.name}] ${it.message}" }
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    containerColor = FloconTheme.colorPalette.secondary,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.ContentCopy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text("Copy All", style = FloconTheme.typography.bodySmall)
                    }
                }

                FloconButton(
                    onClick = onClearLogs,
                    containerColor = FloconTheme.colorPalette.secondary,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.Delete,
                            modifier = Modifier.size(14.dp)
                        )
                        Text("Clear", style = FloconTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (logs.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Info,
                        tint = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.3f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No system logs generated yet",
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
                    )
                }
            }
        } else {
            ConsoleLogPanel(
                logs = logs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

private data class BadgeTheme(
    val label: String,
    val bgColor: Color,
    val textColor: Color,
    val icon: ImageVector
)

@Composable
private fun AboutPane(
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Open Source Licenses",
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Text(
            text = "Flocon is built using open source software. The licenses of libraries used in this project are listed below.",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
        )
        AboutScreen(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary),
        )
    }
}

// ---------------------------------------------------------------------------
// Shared helpers
// ---------------------------------------------------------------------------

@Composable
private fun LogsPane(
    logs: List<LogEntryUiModel>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Flocon System Logs",
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier.weight(1f)
            )

            if (logs.isNotEmpty()) {
                FloconButton(
                    onClick = {
                        val text = logs.joinToString("\n") { "[${it.timestamp} ${it.level.name}] ${it.message}" }
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    containerColor = FloconTheme.colorPalette.secondary,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.ContentCopy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text("Copy All", style = FloconTheme.typography.bodySmall)
                    }
                }

                FloconButton(
                    onClick = onClearLogs,
                    containerColor = FloconTheme.colorPalette.secondary,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.Delete,
                            modifier = Modifier.size(14.dp)
                        )
                        Text("Clear", style = FloconTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (logs.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Info,
                        tint = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.3f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No system logs generated yet",
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
                    )
                }
            }
        } else {
            ConsoleLogPanel(
                logs = logs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

@Composable
private fun AboutPane(
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Open Source Licenses",
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Text(
            text = "Flocon is built using open source software. The licenses of libraries used in this project are listed below.",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
        )
        AboutScreen(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary),
        )
    }
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
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = FloconTheme.shapes.medium

    val bgColor = when {
        selected -> FloconTheme.colorPalette.accent
        hovered -> FloconTheme.colorPalette.secondary
        else -> FloconTheme.colorPalette.secondary.copy(alpha = 0.5f)
    }

    val contentColor = when {
        selected -> FloconTheme.colorPalette.onAccent
        else -> FloconTheme.colorPalette.onPrimary
    }

    val borderColor = if (selected) {
        FloconTheme.colorPalette.onAccent.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    val icon = when (theme) {
        ThemeSetting.Dark -> Icons.Outlined.ModeNight
        ThemeSetting.Light -> Icons.Outlined.LightMode
        ThemeSetting.System -> Icons.Outlined.Computer
    }

    FloconSurface(
        onClick = onClick,
        color = bgColor,
        contentColor = contentColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.height(40.dp),
        interactionSource = interactionSource
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            FloconIcon(
                imageVector = icon,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(
                    when (theme) {
                        ThemeSetting.Dark -> Res.string.settings_theme_dark
                        ThemeSetting.Light -> Res.string.settings_theme_light
                        ThemeSetting.System -> Res.string.settings_theme_system
                    }
                ),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = FloconTheme.typography.bodySmall
            )
        }
    }
}

@Composable
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.secondary)
            .border(1.dp, FloconTheme.colorPalette.primary, FloconTheme.shapes.medium)
            .padding(12.dp)
    ) {
        items(logs) { entry ->
            val color = when (entry.level) {
                LogLevel.ERROR -> FloconTheme.colorPalette.onError
                LogLevel.DEBUG -> FloconTheme.colorPalette.onAccent
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "[${entry.timestamp}]",
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.4f),
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
                Text(
                    text = entry.level.name,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.width(48.dp)
                )
                Text(
                    text = entry.message,
                    color = FloconTheme.colorPalette.onPrimary,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1f)
                )
            }
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
