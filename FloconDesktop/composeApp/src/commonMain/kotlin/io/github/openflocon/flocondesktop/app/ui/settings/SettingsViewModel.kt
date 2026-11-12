package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_success
import flocondesktop.composeapp.generated.resources.settings_data_media_clean_failed
import flocondesktop.composeapp.generated.resources.settings_data_purge_failed
import flocondesktop.composeapp.generated.resources.settings_server_ports_applied
import flocondesktop.composeapp.generated.resources.settings_test_failure
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.maintenance.usecase.CleanMediaFilesUseCase
import io.github.openflocon.domain.maintenance.usecase.GetDatabaseSizeUseCase
import io.github.openflocon.domain.maintenance.usecase.GetMediaFilesSizeUseCase
import io.github.openflocon.domain.maintenance.usecase.ObserveAutoMediaCleanupIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.ObserveAutoPurgeIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.PurgeDatabaseUseCase
import io.github.openflocon.domain.maintenance.usecase.SetAutoMediaCleanupIntervalUseCase
import io.github.openflocon.domain.maintenance.usecase.SetAutoPurgeIntervalUseCase
import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.SetStartScreenUseCase
import io.github.openflocon.domain.menu.usecase.ToggleHiddenMenuItemUseCase
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.domain.models.maintenance.PurgeInterval
import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.ServerPortsError
import io.github.openflocon.domain.settings.usecase.SetAppScaleUseCase
import io.github.openflocon.domain.settings.usecase.SetMascotEnabledUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.domain.settings.usecase.UpdateServerPortsUseCase
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.id
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.hideableMenuItems
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.startScreenCandidates
import io.github.openflocon.flocondesktop.common.log.LogLevel
import io.github.openflocon.flocondesktop.common.log.LogManagerEditable
import io.github.openflocon.flocondesktop.common.log.toUiModel
import io.github.openflocon.flocondesktop.common.utils.FolderPicker
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import io.github.openflocon.flocondesktop.telemetry.TelemetryManager
import io.github.openflocon.flocondesktop.telemetry.TelemetrySetting
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.io.File

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    observeAppScaleUseCase: ObserveAppScaleUseCase,
    private val setAppScaleUseCase: SetAppScaleUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    observeMascotEnabledUseCase: ObserveMascotEnabledUseCase,
    private val observeServerPortsUseCase: ObserveServerPortsUseCase,
    private val updateServerPortsUseCase: UpdateServerPortsUseCase,
    private val setMascotEnabledUseCase: SetMascotEnabledUseCase,
    observeMenuPreferencesUseCase: ObserveMenuPreferencesUseCase,
    private val toggleHiddenMenuItemUseCase: ToggleHiddenMenuItemUseCase,
    private val setStartScreenUseCase: SetStartScreenUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
    private val logManager: LogManagerEditable,
    private val serverPortsOverrides: ServerPortsOverrides,
    private val getDatabaseSizeUseCase: GetDatabaseSizeUseCase,
    private val purgeDatabaseUseCase: PurgeDatabaseUseCase,
    observeAutoPurgeIntervalUseCase: ObserveAutoPurgeIntervalUseCase,
    private val setAutoPurgeIntervalUseCase: SetAutoPurgeIntervalUseCase,
    private val getMediaFilesSizeUseCase: GetMediaFilesSizeUseCase,
    private val cleanMediaFilesUseCase: CleanMediaFilesUseCase,
    observeAutoMediaCleanupIntervalUseCase: ObserveAutoMediaCleanupIntervalUseCase,
    private val setAutoMediaCleanupIntervalUseCase: SetAutoMediaCleanupIntervalUseCase,
    private val telemetryManager: TelemetryManager,
) : ViewModel() {

    private val _dataState = MutableStateFlow(DataUiState())
    val dataState = _dataState.asStateFlow()

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    private val _floconDocumentsPathInput = MutableStateFlow(
        settingsRepository.getFloconDocumentsPath() ?: GetFloconDocumentPathUseCase.defaultPath().absolutePath
    )
    val floconDocumentsPathInput = _floconDocumentsPathInput.asStateFlow()

    private val _serverPortsForm = MutableStateFlow(
        ServerPortsDomainModel().let {
            ServerPortsFormUiModel(
                websocketPort = it.websocketPort.toString(),
                httpPort = it.httpPort.toString(),
                websocketPortOverridden = serverPortsOverrides.websocketPort != null,
                httpPortOverridden = serverPortsOverrides.httpPort != null,
            )
        },
    )
    val serverPortsForm = _serverPortsForm.asStateFlow()

    private val selectedLogDomains = MutableStateFlow(LogDomain.entries.toSet())
    private val selectedLogLevels = MutableStateFlow(LogLevel.entries.toSet())

    private val filteredLogs = combine(
        logManager.logs,
        selectedLogDomains,
        selectedLogLevels,
    ) { logs, domains, levels ->
        logs.filter { it.domain in domains && it.level in levels }
    }

    val uiState = combine(
        observeAppScaleUseCase(),
        combine(observeThemeUseCase(), observeMascotEnabledUseCase()) { theme, mascotEnabled -> theme to mascotEnabled },
        filteredLogs,
        settingsRepository.adbForwardStatus,
        combine(selectedLogDomains, selectedLogLevels, observeMenuPreferencesUseCase()) { domains, levels, menuPreferences ->
            Triple(domains, levels, menuPreferences)
        },
    ) { appScale, (theme, mascotEnabled), logs, forwardStatus, (domains, levels, menuPreferences) ->
        val startScreenOptions = startScreenCandidates(menuPreferences.hiddenItems)
        SettingsUiState(
            appScale = appScale,
            theme = theme,
            mascotEnabled = mascotEnabled,
            logs = logs.map { it.toUiModel() }.toImmutableList(),
            adbForwardStatus = forwardStatus,
            selectedLogDomains = domains,
            selectedLogLevels = levels,
            menuItems = hideableMenuItems()
                .map { it.toVisibilityUiModel(menuPreferences.hiddenItems) }
                .toImmutableList(),
            startScreenOptions = startScreenOptions.map { it.toVisibilityUiModel(menuPreferences.hiddenItems) }.toImmutableList(),
            // a hidden screen can't be the start one anymore: the default one is used
            startScreenId = startScreenOptions
                .firstOrNull { it.screen.id == menuPreferences.startScreen }
                ?.screen?.id
                ?: SubScreen.Timeline.id,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                appScale = AppScaleSetting.DEFAULT,
                theme = ThemeSetting.DEFAULT,
                mascotEnabled = true,
                logs = persistentListOf(),
                adbForwardStatus = AdbForwardStatus.UNKNOWN,
                selectedLogDomains = LogDomain.entries.toSet(),
                selectedLogLevels = LogLevel.entries.toSet(),
            )
        )

    init {
        viewModelScope.launch {
            // the fields follow the effective ports (saved ones, or the launch options)
            observeServerPortsUseCase().collect { ports ->
                _serverPortsForm.update {
                    it.copy(websocketPort = ports.websocketPort.toString(), httpPort = ports.httpPort.toString())
                }
            }
        }
        viewModelScope.launch {
            observeAutoPurgeIntervalUseCase().collect { interval ->
                _dataState.update { it.copy(autoPurgeInterval = interval) }
            }
        }
        viewModelScope.launch {
            observeAutoMediaCleanupIntervalUseCase().collect { interval ->
                _dataState.update { it.copy(autoMediaCleanupInterval = interval) }
            }
        }
        viewModelScope.launch {
            // Utiliser GlobalScope ici pour la simplicité de l'exemple, mais préférez un scope dédié
            settingsRepository.adbPath.collect { path ->
                path?.let { _adbPathInput.value = it }
            }
        }
        viewModelScope.launch {
            settingsRepository.floconDocumentsPath.collect { path ->
                path?.let { _floconDocumentsPathInput.value = it }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.AppScaleChange -> onAppScaleChange(action)
            is SettingsAction.ThemeChange -> onThemeChange(action)
            is SettingsAction.MascotEnabledChange -> onMascotEnabledChange(action)
            is SettingsAction.ToggleMenuItem -> onToggleMenuItem(action)
            is SettingsAction.StartScreenChange -> onStartScreenChange(action)
        }
    }

    private fun onStartScreenChange(action: SettingsAction.StartScreenChange) {
        viewModelScope.launch {
            setStartScreenUseCase(action.screenId)
        }
    }

    private fun onToggleMenuItem(action: SettingsAction.ToggleMenuItem) {
        viewModelScope.launch {
            toggleHiddenMenuItemUseCase(action.itemId)
        }
    }

    private fun onAppScaleChange(action: SettingsAction.AppScaleChange) {
        viewModelScope.launch {
            setAppScaleUseCase(action.value)
        }
    }

    private fun onThemeChange(action: SettingsAction.ThemeChange) {
        viewModelScope.launch {
            setThemeUseCase(action.value)
        }
    }

    private fun onMascotEnabledChange(action: SettingsAction.MascotEnabledChange) {
        telemetryManager.settingChanged(TelemetrySetting.MascotEnabled, action.enabled)
        viewModelScope.launch {
            setMascotEnabledUseCase(action.enabled)
        }
    }

    fun onWebsocketPortChanged(value: String) {
        _serverPortsForm.update { it.copy(websocketPort = value.toPortInput(), error = null) }
    }

    fun onHttpPortChanged(value: String) {
        _serverPortsForm.update { it.copy(httpPort = value.toPortInput(), error = null) }
    }

    /** Puts the default ports back (the ones forced by a launch option are left alone) and applies them. */
    fun resetServerPorts() {
        val defaults = ServerPortsDomainModel()
        _serverPortsForm.update {
            it.copy(
                websocketPort = if (it.websocketPortOverridden) it.websocketPort else defaults.websocketPort.toString(),
                httpPort = if (it.httpPortOverridden) it.httpPort else defaults.httpPort.toString(),
                error = null,
            )
        }
        applyServerPorts()
    }

    fun applyServerPorts() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val form = _serverPortsForm.value
            // nothing to save: both ports are forced by the launch options
            if (form.websocketPortOverridden && form.httpPortOverridden) return@launch
            val websocketPort = form.websocketPort.toIntOrNull()
            val httpPort = form.httpPort.toIntOrNull()
            if (websocketPort == null || httpPort == null) {
                // an empty field is not a valid port
                _serverPortsForm.update { it.copy(error = ServerPortsError.OutOfRange) }
                return@launch
            }
            updateServerPortsUseCase(ServerPortsDomainModel(websocketPort = websocketPort, httpPort = httpPort)).fold(
                doOnFailure = { error -> _serverPortsForm.update { it.copy(error = error) } },
                doOnSuccess = {
                    logManager.d(TAG, "Server ports applied: $websocketPort / $httpPort", domain = LogDomain.Default)
                    feedbackDisplayer.displayMessage(getString(Res.string.settings_server_ports_applied))
                },
            )
        }
    }

    // digits only, 5 at most
    private fun String.toPortInput() = filter { it.isDigit() }.take(5)

    fun onAdbPathChanged(newPath: String) {
        _adbPathInput.value = newPath
    }

    fun saveAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveAdb()
        }
    }

    private suspend fun saveAdb() {
        val path = adbPathInput.value
        Logger.d(TAG) { "Saving ADB path: $path" }
        settingsRepository.setAdbPath(path)
        logManager.d(TAG, "Saving ADB path: $path", domain = LogDomain.Default)
    }

    fun testAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveAdb()
            val path = adbPathInput.value
            Logger.d(TAG) { "Testing ADB path: $path" }
            logManager.d(TAG, "Testing ADB path: $path", domain = LogDomain.Default)
            testAdbUseCase().fold(
                doOnFailure = {
                    val msg = "ADB test failed: ${it.message}"
                    Logger.e(TAG, it) { msg }
                    logManager.e(TAG, "ADB test failed", it, domain = LogDomain.Default)
                    feedbackDisplayer.displayMessage(
                        message = getString(Res.string.settings_test_failure, it.localizedMessage),
                        type = FeedbackDisplayer.MessageType.Error
                    )
                    initialSetupStateHolder.setRequiresInitialSetup()
                },
                doOnSuccess = {
                    Logger.d(TAG) { "ADB test succeeded" }
                    logManager.d(TAG, "ADB test succeeded", domain = LogDomain.Default)
                    feedbackDisplayer.displayMessage(getString(Res.string.general_success))
                    initialSetupStateHolder.setAdbIsWorking()
                },
            )
        }
    }

    fun toggleLogDomain(domain: LogDomain) {
        selectedLogDomains.update { if (domain in it) it - domain else it + domain }
    }

    fun toggleLogLevel(level: LogLevel) {
        selectedLogLevels.update { if (level in it) it - level else it + level }
    }

    fun clearLogs() {
        logManager.clear()
    }

    fun onFloconDocumentsPathChanged(newPath: String) {
        _floconDocumentsPathInput.value = newPath
    }

    fun saveFloconDocumentsPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val path = _floconDocumentsPathInput.value.trim()
            val finalPath = path.ifBlank { GetFloconDocumentPathUseCase.defaultPath().absolutePath }
            Logger.d(TAG) { "Saving Flocon documents path: $finalPath" }
            logManager.d(TAG, "Saving Flocon documents path: $finalPath", domain = LogDomain.Default)
            settingsRepository.setFloconDocumentsPath(finalPath)
            _floconDocumentsPathInput.value = finalPath
            feedbackDisplayer.displayMessage(getString(Res.string.general_success))
        }
    }

    fun pickFloconDocumentsFolder() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val currentPath = _floconDocumentsPathInput.value.ifBlank {
                GetFloconDocumentPathUseCase.defaultPath().absolutePath
            }
            val selected = FolderPicker.pickFolder(
                title = "Select Flocon Documents Folder",
                initialPath = currentPath,
            )
            if (selected != null) {
                val path = selected.absolutePath
                Logger.d(TAG) { "Picked Flocon documents folder: $path" }
                logManager.d(TAG, "Picked Flocon documents folder: $path", domain = LogDomain.Default)
                _floconDocumentsPathInput.value = path
                settingsRepository.setFloconDocumentsPath(path)
                feedbackDisplayer.displayMessage(getString(Res.string.general_success))
            }
        }
    }

    fun resetFloconDocumentsPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val defaultPath = GetFloconDocumentPathUseCase.defaultPath().absolutePath
            Logger.d(TAG) { "Resetting Flocon documents path to: $defaultPath" }
            logManager.d(TAG, "Resetting Flocon documents path to: $defaultPath", domain = LogDomain.Default)
            _floconDocumentsPathInput.value = defaultPath
            settingsRepository.setFloconDocumentsPath(defaultPath)
            feedbackDisplayer.displayMessage(getString(Res.string.general_success))
        }
    }

    fun openFloconDocumentsFolder() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val path = _floconDocumentsPathInput.value.ifBlank {
                GetFloconDocumentPathUseCase.defaultPath().absolutePath
            }
            val folder = File(path)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            OpenFile.openFileOnDesktop(folder.absolutePath).alsoFailure {
                feedbackDisplayer.displayMessage(it.message)
            }
        }
    }

    /** Computes the size of the database, called each time the Data screen is displayed. */
    fun refreshDatabaseSize() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val size = getDatabaseSizeUseCase()
            _dataState.update { it.copy(databaseSizeBytes = size) }
        }
    }

    /** Computes the size of the saved screenshots and recordings, called each time the Data screen is displayed. */
    fun refreshMediaSize() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val size = getMediaFilesSizeUseCase()
            _dataState.update {
                it.copy(
                    screenshotsSizeBytes = size.screenshotsBytes,
                    recordingsSizeBytes = size.recordingsBytes,
                )
            }
        }
    }

    fun onAutoMediaCleanupIntervalChanged(interval: AutoPurgeInterval) {
        viewModelScope.launch {
            setAutoMediaCleanupIntervalUseCase(interval)
        }
    }

    fun onMediaCleanupIntervalChanged(interval: PurgeInterval) {
        _dataState.update { it.copy(mediaCleanupInterval = interval) }
    }

    fun cleanMediaFiles() {
        if (_dataState.value.isCleaningMedia) return
        val interval = _dataState.value.mediaCleanupInterval
        _dataState.update { it.copy(isCleaningMedia = true) }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            try {
                cleanMediaFilesUseCase(interval)
                logManager.d(TAG, "Screenshots and recordings cleaned: ${interval.name}", domain = LogDomain.Default)
                feedbackDisplayer.displayMessage(getString(Res.string.general_success))
            } catch (t: Throwable) {
                Logger.e(TAG, t) { "Media files cleanup failed" }
                logManager.e(TAG, "Media files cleanup failed", t, domain = LogDomain.Default)
                feedbackDisplayer.displayMessage(
                    message = t.localizedMessage ?: getString(Res.string.settings_data_media_clean_failed),
                    type = FeedbackDisplayer.MessageType.Error,
                )
            } finally {
                _dataState.update { it.copy(isCleaningMedia = false) }
                refreshMediaSize()
            }
        }
    }

    fun onAutoPurgeIntervalChanged(interval: AutoPurgeInterval) {
        viewModelScope.launch {
            setAutoPurgeIntervalUseCase(interval)
        }
    }

    fun onPurgeIntervalChanged(interval: PurgeInterval) {
        _dataState.update { it.copy(purgeInterval = interval) }
    }

    fun purgeDatabase() {
        if (_dataState.value.isPurging) return
        val interval = _dataState.value.purgeInterval
        _dataState.update { it.copy(isPurging = true) }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            try {
                purgeDatabaseUseCase(interval)
                logManager.d(TAG, "Database purged: ${interval.name}", domain = LogDomain.Default)
                feedbackDisplayer.displayMessage(getString(Res.string.general_success))
            } catch (t: Throwable) {
                Logger.e(TAG, t) { "Database purge failed" }
                logManager.e(TAG, "Database purge failed", t, domain = LogDomain.Default)
                feedbackDisplayer.displayMessage(
                    message = t.localizedMessage ?: getString(Res.string.settings_data_purge_failed),
                    type = FeedbackDisplayer.MessageType.Error,
                )
            } finally {
                _dataState.update { it.copy(isPurging = false) }
                refreshDatabaseSize()
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
