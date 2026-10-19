package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_success
import flocondesktop.composeapp.generated.resources.settings_test_failure
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.SetFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.common.log.LogDomain
import io.github.openflocon.flocondesktop.common.log.LogLevel
import io.github.openflocon.flocondesktop.common.log.LogManager
import io.github.openflocon.flocondesktop.common.log.toUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.flocondesktop.common.utils.FolderPicker
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import java.io.File
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    fontSizeMultiplierUseCase: ObserveFontSizeMultiplierUseCase,
    private val setFontSizeMultiplierUseCase: SetFontSizeMultiplierUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
    private val logManager: LogManager,
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    private val _floconDocumentsPathInput = MutableStateFlow(
        settingsRepository.getFloconDocumentsPath() ?: GetFloconDocumentPathUseCase.defaultPath().absolutePath
    )
    val floconDocumentsPathInput = _floconDocumentsPathInput.asStateFlow()

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
        fontSizeMultiplierUseCase(),
        observeThemeUseCase(),
        filteredLogs,
        settingsRepository.adbForwardStatus,
        combine(selectedLogDomains, selectedLogLevels) { domains, levels -> domains to levels },
    ) { multiplier, theme, logs, forwardStatus, (domains, levels) ->
        SettingsUiState(
            fontSizeMultiplier = multiplier,
            theme = theme,
            logs = logs.map { it.toUiModel() }.toImmutableList(),
            adbForwardStatus = forwardStatus,
            selectedLogDomains = domains,
            selectedLogLevels = levels,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                fontSizeMultiplier = 1f,
                theme = ThemeSetting.DEFAULT,
                logs = persistentListOf(),
                adbForwardStatus = AdbForwardStatus.UNKNOWN,
                selectedLogDomains = LogDomain.entries.toSet(),
                selectedLogLevels = LogLevel.entries.toSet(),
            )
        )

    init {
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
            is SettingsAction.FontSizeMultiplierChange -> onFontSizeMultiplierChange(action)
            is SettingsAction.ThemeChange -> onThemeChange(action)
        }
    }

    private fun onFontSizeMultiplierChange(action: SettingsAction.FontSizeMultiplierChange) {
        viewModelScope.launch {
            setFontSizeMultiplierUseCase(action.value)
        }
    }

    private fun onThemeChange(action: SettingsAction.ThemeChange) {
        viewModelScope.launch {
            setThemeUseCase(action.value)
        }
    }

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
        logManager.d(TAG, "Saving ADB path: $path", LogDomain.Default)
    }

    fun testAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveAdb()
            val path = adbPathInput.value
            Logger.d(TAG) { "Testing ADB path: $path" }
            logManager.d(TAG, "Testing ADB path: $path", LogDomain.Default)
            testAdbUseCase().fold(
                doOnFailure = {
                    val msg = "ADB test failed: ${it.message}"
                    Logger.e(TAG, it) { msg }
                    logManager.e(TAG, "ADB test failed", it, LogDomain.Default)
                    feedbackDisplayer.displayMessage(
                        message = getString(Res.string.settings_test_failure, it.localizedMessage),
                        type = FeedbackDisplayer.MessageType.Error
                    )
                    initialSetupStateHolder.setRequiresInitialSetup()
                },
                doOnSuccess = {
                    Logger.d(TAG) { "ADB test succeeded" }
                    logManager.d(TAG, "ADB test succeeded", LogDomain.Default)
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
            logManager.d(TAG, "Saving Flocon documents path: $finalPath", LogDomain.Default)
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
                logManager.d(TAG, "Picked Flocon documents folder: $path", LogDomain.Default)
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
            logManager.d(TAG, "Resetting Flocon documents path to: $defaultPath", LogDomain.Default)
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

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
