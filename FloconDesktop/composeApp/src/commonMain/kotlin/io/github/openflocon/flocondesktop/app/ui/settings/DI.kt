package io.github.openflocon.flocondesktop.app.ui.settings

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val settingsModule = module {
    // viewModelOf only supports up to 22 constructor parameters, SettingsViewModel has more
    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
            testAdbUseCase = get(),
            observeAppScaleUseCase = get(),
            setAppScaleUseCase = get(),
            observeThemeUseCase = get(),
            setThemeUseCase = get(),
            observeMascotEnabledUseCase = get(),
            setMascotEnabledUseCase = get(),
            observeMenuPreferencesUseCase = get(),
            toggleHiddenMenuItemUseCase = get(),
            setStartScreenUseCase = get(),
            feedbackDisplayer = get(),
            initialSetupStateHolder = get(),
            dispatcherProvider = get(),
            logManager = get(),
            getDatabaseSizeUseCase = get(),
            purgeDatabaseUseCase = get(),
            observeAutoPurgeIntervalUseCase = get(),
            setAutoPurgeIntervalUseCase = get(),
            getMediaFilesSizeUseCase = get(),
            cleanMediaFilesUseCase = get(),
            observeAutoMediaCleanupIntervalUseCase = get(),
            setAutoMediaCleanupIntervalUseCase = get(),
        )
    }
}
