package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
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
import flocondesktop.composeapp.generated.resources.general_clear
import flocondesktop.composeapp.generated.resources.general_copy_all
import flocondesktop.composeapp.generated.resources.general_open_folder
import flocondesktop.composeapp.generated.resources.general_pick
import flocondesktop.composeapp.generated.resources.general_reset_default
import flocondesktop.composeapp.generated.resources.general_save
import flocondesktop.composeapp.generated.resources.settings_adb_configuration
import flocondesktop.composeapp.generated.resources.settings_adb_description
import flocondesktop.composeapp.generated.resources.settings_adb_executable_path
import flocondesktop.composeapp.generated.resources.settings_adb_reverse_forwarding
import flocondesktop.composeapp.generated.resources.settings_adb_setup_title
import flocondesktop.composeapp.generated.resources.settings_adb_valid
import flocondesktop.composeapp.generated.resources.settings_documents_description
import flocondesktop.composeapp.generated.resources.settings_documents_folder_path
import flocondesktop.composeapp.generated.resources.settings_licenses_description
import flocondesktop.composeapp.generated.resources.settings_mascot_description
import flocondesktop.composeapp.generated.resources.settings_mascot_title
import flocondesktop.composeapp.generated.resources.settings_no_logs
import flocondesktop.composeapp.generated.resources.settings_open_source_licenses
import flocondesktop.composeapp.generated.resources.settings_reverse_forwarding_description
import flocondesktop.composeapp.generated.resources.settings_scale_description
import flocondesktop.composeapp.generated.resources.settings_scale_title
import flocondesktop.composeapp.generated.resources.settings_system_logs_title
import flocondesktop.composeapp.generated.resources.settings_test
import flocondesktop.composeapp.generated.resources.settings_theme
import flocondesktop.composeapp.generated.resources.settings_theme_dark
import flocondesktop.composeapp.generated.resources.settings_theme_description
import flocondesktop.composeapp.generated.resources.settings_theme_light
import flocondesktop.composeapp.generated.resources.settings_theme_system
import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.flocondesktop.app.ui.releasenotes.AllReleaseNotesPane
import io.github.openflocon.flocondesktop.common.log.LogEntryUiModel
import io.github.openflocon.flocondesktop.common.log.LogLevel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconSwitch
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import io.github.openflocon.library.designsystem.theme.AppScale
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ---------------------------------------------------------------------------
// Tabs
// ---------------------------------------------------------------------------

private enum class SettingsTab(val label: String, val icon: ImageVector) {
    General("General", Icons.Outlined.Folder),
    Adb("Adb", Icons.Outlined.Cable),
    Appearance("Appearance", Icons.Outlined.TextFields),
    Logs("Logs", Icons.AutoMirrored.Outlined.List),
    ReleaseNotes("Release notes", Icons.Outlined.NewReleases),
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
    val floconDocumentsPathText by viewModel.floconDocumentsPathInput.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        adbPathText = adbPathText,
        onAdbPathChanged = viewModel::onAdbPathChanged,
        saveAdbPath = viewModel::saveAdbPath,
        testAdbPath = viewModel::testAdbPath,
        floconDocumentsPathText = floconDocumentsPathText,
        onFloconDocumentsPathChanged = viewModel::onFloconDocumentsPathChanged,
        onPickFloconDocumentsFolder = viewModel::pickFloconDocumentsFolder,
        onSaveFloconDocumentsPath = viewModel::saveFloconDocumentsPath,
        onResetFloconDocumentsPath = viewModel::resetFloconDocumentsPath,
        onOpenFloconDocumentsFolder = viewModel::openFloconDocumentsFolder,
        onAction = viewModel::onAction,
        onClearLogs = viewModel::clearLogs,
        onToggleLogDomain = viewModel::toggleLogDomain,
        onToggleLogLevel = viewModel::toggleLogLevel,
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
    floconDocumentsPathText: String,
    onFloconDocumentsPathChanged: (String) -> Unit,
    onPickFloconDocumentsFolder: () -> Unit,
    onSaveFloconDocumentsPath: () -> Unit,
    onResetFloconDocumentsPath: () -> Unit,
    onOpenFloconDocumentsFolder: () -> Unit,
    needsAdbSetup: Boolean,
    onAction: (SettingsAction) -> Unit,
    onClearLogs: () -> Unit,
    onToggleLogDomain: (LogDomain) -> Unit,
    onToggleLogLevel: (LogLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(if (needsAdbSetup) SettingsTab.Adb else SettingsTab.General) }

    Row(modifier = modifier) {
        // ── Drawer ──────────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .padding(FloconTheme.spacing.medium)
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
            color = FloconTheme.colors.line,
        )

        // ── Content pane ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(FloconTheme.spacing.large)
        ) {
            when (selectedTab) {
                SettingsTab.General -> GeneralPane(
                    floconDocumentsPath = floconDocumentsPathText,
                    onFloconDocumentsPathChanged = onFloconDocumentsPathChanged,
                    onPickFolder = onPickFloconDocumentsFolder,
                    onSavePath = onSaveFloconDocumentsPath,
                    onResetPath = onResetFloconDocumentsPath,
                    onOpenFolder = onOpenFloconDocumentsFolder,
                )

                SettingsTab.Adb -> AdbPane(
                    adbPathText = adbPathText,
                    onAdbPathChanged = onAdbPathChanged,
                    saveAdbPath = saveAdbPath,
                    testAdbPath = testAdbPath,
                    needsAdbSetup = needsAdbSetup,
                    adbForwardStatus = uiState.adbForwardStatus,
                )

                SettingsTab.Appearance -> AppearancePane(
                    appScale = uiState.appScale,
                    currentTheme = uiState.theme,
                    mascotEnabled = uiState.mascotEnabled,
                    onAction = onAction,
                )

                SettingsTab.Logs -> LogsPane(
                    logs = uiState.logs,
                    selectedDomains = uiState.selectedLogDomains,
                    selectedLevels = uiState.selectedLogLevels,
                    onToggleDomain = onToggleLogDomain,
                    onToggleLevel = onToggleLogLevel,
                    onClearLogs = onClearLogs,
                )

                SettingsTab.ReleaseNotes -> AllReleaseNotesPane()

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
        selected -> FloconTheme.colors.accentSoft
        hovered -> FloconTheme.colors.bg300
        else -> Color.Transparent
    }

    val contentColor = when {
        selected -> FloconTheme.colors.accent
        hovered -> FloconTheme.colors.ink
        else -> FloconTheme.colors.inkMuted
    }

    FloconSurface(
        onClick = onClick,
        color = bgColor,
        contentColor = contentColor,
        shape = shape,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium)
        ) {
            FloconIcon(
                imageVector = tab.icon,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = tab.label,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable Settings Card
// ---------------------------------------------------------------------------

private val PanelShape = RoundedCornerShape(12.dp)

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
        color = FloconTheme.colors.bg100,
        shape = PanelShape,
        border = BorderStroke(1.dp, FloconTheme.colors.line),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(FloconTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                FloconIcon(
                    imageVector = icon,
                    tint = FloconTheme.colors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FloconTheme.colors.ink,
                    modifier = Modifier.weight(1f)
                )
                headerActions?.invoke(this)
            }

            if (description != null) {
                Text(
                    text = description,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkMuted,
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
private fun GeneralPane(
    floconDocumentsPath: String,
    onFloconDocumentsPathChanged: (String) -> Unit,
    onPickFolder: () -> Unit,
    onSavePath: () -> Unit,
    onResetPath: () -> Unit,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
        modifier = modifier
    ) {
        SettingsCard(
            title = "Flocon Documents",
            icon = Icons.Outlined.Folder,
            description = stringResource(Res.string.settings_documents_description)
        ) {
            Text(
                text = stringResource(Res.string.settings_documents_folder_path),
                style = FloconTheme.typography.labelSmall,
                color = FloconTheme.colors.inkMuted
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                FloconTextFieldWithoutM3(
                    value = floconDocumentsPath,
                    onValueChange = onFloconDocumentsPathChanged,
                    placeholder = defaultPlaceHolder("~/Documents/Flocon"),
                    containerColor = FloconTheme.colors.bg200,
                    contentPadding = PaddingValues(FloconTheme.spacing.medium),
                    modifier = Modifier.weight(1f)
                )

                SettingsButton(
                    text = stringResource(Res.string.general_pick),
                    onClick = onPickFolder,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
                SettingsButton(
                    text = stringResource(Res.string.general_save),
                    onClick = onSavePath,
                    primary = true,
                )
                SettingsButton(
                    text = stringResource(Res.string.general_reset_default),
                    onClick = onResetPath,
                )
                SettingsButton(
                    text = stringResource(Res.string.general_open_folder),
                    onClick = onOpenFolder,
                )
            }
        }
    }
}

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
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
        modifier = modifier
    ) {
        SettingsCard(
            title = stringResource(Res.string.settings_adb_configuration),
            icon = Icons.Outlined.Settings,
            description = stringResource(Res.string.settings_adb_description)
        ) {
            // Setup alert or status
            if (needsAdbSetup) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colors.catCrashSoft)
                        .padding(FloconTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Warning,
                        tint = FloconTheme.colors.statusError,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(Res.string.settings_adb_setup_title),
                        color = FloconTheme.colors.statusError,
                        style = FloconTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colors.statusSuccess.copy(alpha = 0.12f))
                        .padding(FloconTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colors.statusSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(Res.string.settings_adb_valid),
                        color = FloconTheme.colors.statusSuccess,
                        style = FloconTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(FloconTheme.spacing.extraSmall))

            Text(
                text = stringResource(Res.string.settings_adb_executable_path),
                style = FloconTheme.typography.labelSmall,
                color = FloconTheme.colors.inkMuted
            )

            FloconTextFieldWithoutM3(
                value = adbPathText,
                onValueChange = onAdbPathChanged,
                placeholder = defaultPlaceHolder("Eg: /Users/youruser/Library/Android/sdk/platform-tools/adb"),
                containerColor = FloconTheme.colors.bg200,
                contentPadding = PaddingValues(FloconTheme.spacing.medium),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
                SettingsButton(
                    text = stringResource(Res.string.general_save),
                    onClick = saveAdbPath,
                    primary = true,
                )
                SettingsButton(
                    text = stringResource(Res.string.settings_test),
                    onClick = testAdbPath,
                )
            }
        }

        SettingsCard(
            title = stringResource(Res.string.settings_adb_reverse_forwarding),
            icon = Icons.Outlined.Cable,
            description = stringResource(Res.string.settings_reverse_forwarding_description)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.small)
                    .background(FloconTheme.colors.bg200)
                    .padding(FloconTheme.spacing.medium)
            ) {
                AdbForwardStatusBadge(status = adbForwardStatus)

                Text(
                    text = when (adbForwardStatus) {
                        AdbForwardStatus.OK -> "Reverse port forwarding is active and healthy."
                        AdbForwardStatus.NOK -> "Connection failed. Please ensure ADB is configured correctly and your device is connected."
                        AdbForwardStatus.UNKNOWN -> "Status unknown. Waiting for device or forwarding loop to initialize."
                    },
                    color = FloconTheme.colors.inkMuted,
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
            FloconTheme.colors.statusSuccess.copy(alpha = 0.15f),
            FloconTheme.colors.statusSuccess,
            Icons.Outlined.Check
        )

        AdbForwardStatus.NOK -> BadgeTheme(
            "FAILED",
            FloconTheme.colors.catCrashSoft,
            FloconTheme.colors.statusError,
            Icons.Outlined.ErrorOutline
        )

        AdbForwardStatus.UNKNOWN -> BadgeTheme(
            "PENDING",
            FloconTheme.colors.bg300,
            FloconTheme.colors.inkMuted,
            Icons.Outlined.Info
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(theme.bgColor)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall)
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
    appScale: AppScaleSetting,
    currentTheme: ThemeSetting,
    mascotEnabled: Boolean,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
        modifier = modifier
    ) {
        SettingsCard(
            title = stringResource(Res.string.settings_theme),
            icon = Icons.Outlined.ModeNight,
            description = stringResource(Res.string.settings_theme_description)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
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
            title = stringResource(Res.string.settings_scale_title),
            icon = Icons.Outlined.TextFields,
            description = stringResource(Res.string.settings_scale_description)
        ) {
            ScaleDropdown(
                selected = appScale.toAppScale(),
                onSelect = { onAction(SettingsAction.AppScaleChange(it.toSetting())) },
            )
        }

        SettingsCard(
            title = stringResource(Res.string.settings_mascot_title),
            icon = Icons.Outlined.Pets,
            description = stringResource(Res.string.settings_mascot_description)
        ) {
            FloconSwitch(
                checked = mascotEnabled,
                onCheckedChange = { onAction(SettingsAction.MascotEnabledChange(it)) },
            )
        }
    }
}

@Composable
private fun ScaleDropdown(
    selected: AppScale,
    onSelect: (AppScale) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { expanded = true },
        modifier = modifier,
        anchorContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                modifier = Modifier
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colors.bg200)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colors.lineStrong,
                        shape = FloconTheme.shapes.medium,
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
            ) {
                Text(
                    text = "${selected.label} (${selected.percent}%)",
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.ink,
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null,
                    tint = FloconTheme.colors.inkMuted,
                )
            }
        },
    ) {
        AppScale.entries.forEach { scale ->
            FloconDropdownMenuItem(
                text = "${scale.label} (${scale.percent}%)",
                leadingIcon = if (scale == selected) Icons.Outlined.Check else null,
                onClick = {
                    expanded = false
                    onSelect(scale)
                },
            )
        }
    }
}

@Composable
private fun LogsPane(
    logs: ImmutableList<LogEntryUiModel>,
    selectedDomains: Set<LogDomain>,
    selectedLevels: Set<LogLevel>,
    onToggleDomain: (LogDomain) -> Unit,
    onToggleLevel: (LogLevel) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
        ) {
            Text(
                text = stringResource(Res.string.settings_system_logs_title),
                style = FloconTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
                modifier = Modifier.weight(1f)
            )

            if (logs.isNotEmpty()) {
                FloconButton(
                    onClick = {
                        val text = logs.joinToString("\n") { "[${it.timestamp} ${it.level.name} ${it.domain.name}] ${it.message}" }
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    containerColor = FloconTheme.colors.bg200,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.ContentCopy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(stringResource(Res.string.general_copy_all), style = FloconTheme.typography.bodySmall)
                    }
                }

                FloconButton(
                    onClick = onClearLogs,
                    containerColor = FloconTheme.colors.bg200,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact)
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.Delete,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(stringResource(Res.string.general_clear), style = FloconTheme.typography.bodySmall)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LogFilterGroup(
                title = "Domain",
                options = LogDomain.entries,
                isSelected = { it in selectedDomains },
                label = { it.name },
                onToggle = onToggleDomain,
            )
            LogFilterGroup(
                title = "Level",
                options = LogLevel.entries,
                isSelected = { it in selectedLevels },
                label = { it.name },
                onToggle = onToggleLevel,
            )
        }

        if (logs.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(PanelShape)
                    .background(FloconTheme.colors.bg100)
                    .border(1.dp, FloconTheme.colors.line, PanelShape)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Info,
                        tint = FloconTheme.colors.inkFaint,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(Res.string.settings_no_logs),
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colors.inkFaint,
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
private fun <T> LogFilterGroup(
    title: String,
    options: List<T>,
    isSelected: (T) -> Boolean,
    label: (T) -> String,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$title:",
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = FloconTheme.colors.ink,
            modifier = Modifier.width(64.dp),
        )
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FloconCheckbox(
                    checked = isSelected(option),
                    onCheckedChange = { onToggle(option) },
                )
                Text(
                    text = label(option),
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.ink,
                )
            }
        }
    }
}

@Composable
private fun AboutPane(
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(Res.string.settings_open_source_licenses),
            style = FloconTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = stringResource(Res.string.settings_licenses_description),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.inkMuted,
        )
        AboutScreen(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(PanelShape)
                .background(FloconTheme.colors.bg100)
                .border(1.dp, FloconTheme.colors.line, PanelShape),
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
    primary: Boolean = false,
) {
    FloconSurface(
        onClick = onClick,
        color = if (primary) FloconTheme.colors.accent else FloconTheme.colors.bg200,
        contentColor = if (primary) FloconTheme.colors.accentInk else FloconTheme.colors.ink,
        shape = FloconTheme.shapes.medium,
        border = if (primary) null else BorderStroke(1.dp, FloconTheme.colors.lineStrong),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
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
        selected -> FloconTheme.colors.accentSoft
        hovered -> FloconTheme.colors.bg300
        else -> FloconTheme.colors.bg200
    }

    val contentColor = when {
        selected -> FloconTheme.colors.accent
        else -> FloconTheme.colors.ink
    }

    val borderColor = if (selected) {
        FloconTheme.colors.accent
    } else {
        FloconTheme.colors.lineStrong
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
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small, Alignment.CenterHorizontally)
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
    logs: ImmutableList<LogEntryUiModel>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.lastIndex)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg000)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.medium)
    ) {
        items(logs) { entry ->
            val color = when (entry.level) {
                LogLevel.ERROR -> FloconTheme.colors.statusError
                LogLevel.INFO -> FloconTheme.colors.ink
                LogLevel.DEBUG -> FloconTheme.colors.statusInfo
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                Text(
                    text = "[${entry.timestamp}]",
                    color = FloconTheme.colors.inkFaint,
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
                    text = entry.domain.name,
                    color = FloconTheme.colors.inkMuted,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.width(56.dp)
                )
                Text(
                    text = entry.message,
                    color = FloconTheme.colors.ink,
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
        var floconPath by remember { mutableStateOf("/Users/user/Documents/Flocon") }
        SettingsScreen(
            uiState = previewSettingsUiState(),
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = { Logger.d { "Save ADB: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB: $adbPath" } },
            floconDocumentsPathText = floconPath,
            onFloconDocumentsPathChanged = { floconPath = it },
            onPickFloconDocumentsFolder = {},
            onSaveFloconDocumentsPath = {},
            onResetFloconDocumentsPath = {},
            onOpenFloconDocumentsFolder = {},
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
            onToggleLogDomain = {},
            onToggleLogLevel = {},
            needsAdbSetup = false,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview_needsAdbSetup() {
    FloconTheme {
        var adbPath by remember { mutableStateOf("/usr/local/bin/adb") }
        var floconPath by remember { mutableStateOf("/Users/user/Documents/Flocon") }
        SettingsScreen(
            uiState = previewSettingsUiState(),
            adbPathText = adbPath,
            onAdbPathChanged = { adbPath = it },
            saveAdbPath = { Logger.d { "Save ADB: $adbPath" } },
            testAdbPath = { Logger.d { "Test ADB: $adbPath" } },
            floconDocumentsPathText = floconPath,
            onFloconDocumentsPathChanged = { floconPath = it },
            onPickFloconDocumentsFolder = {},
            onSaveFloconDocumentsPath = {},
            onResetFloconDocumentsPath = {},
            onOpenFloconDocumentsFolder = {},
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
            onToggleLogDomain = {},
            onToggleLogLevel = {},
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
            floconDocumentsPathText = "/Users/user/Documents/Flocon",
            onFloconDocumentsPathChanged = {},
            onPickFloconDocumentsFolder = {},
            onSaveFloconDocumentsPath = {},
            onResetFloconDocumentsPath = {},
            onOpenFloconDocumentsFolder = {},
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClearLogs = {},
            onToggleLogDomain = {},
            onToggleLogLevel = {},
            needsAdbSetup = false,
        )
    }
}
