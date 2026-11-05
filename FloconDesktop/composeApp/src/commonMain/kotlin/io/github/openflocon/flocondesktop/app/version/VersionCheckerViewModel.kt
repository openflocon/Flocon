package io.github.openflocon.flocondesktop.app.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.new_desktop_version
import flocondesktop.composeapp.generated.resources.update_available_desktop
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.DismissClientVersionUseCase
import io.github.openflocon.domain.versions.usecase.DismissDesktopVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveUpdateAvailableUseCase
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class VersionCheckerViewModel(
    private val checkIsDesktopOnLastVersionUseCase: CheckIsDesktopOnLastVersionUseCase,
    private val dismissDesktopVersionUseCase: DismissDesktopVersionUseCase,
    private val dismissClientVersionUseCase: DismissClientVersionUseCase,
    private val observeIsClientOnLastVersionUseCase: ObserveIsClientOnLastVersionUseCase,
    private val observeUpdateAvailableUseCase: ObserveUpdateAvailableUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    data class UpdateChipUiModel(
        val text: String,
        val link: String,
    )

    data class VersionAvailableUiModel(
        val version: String,
        val link: String,
        val title: String,
        val subtitle: String?,
    )

    data class VersionAvailableState(
        val desktop: VersionAvailableUiModel?,
    )

    /** A newer SDK than the one running in the selected device + app. */
    data class SdkUpdateUiModel(
        val currentVersion: String,
        val latestVersion: String,
        val dependency: String,
        val releaseNotesLink: String,
    )

    val updateChip: StateFlow<UpdateChipUiModel?> = observeUpdateAvailableUseCase(BuildConfig.APP_VERSION)
        .map { update ->
            when (update) {
                is ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.DesktopUpdate -> {
                    UpdateChipUiModel(
                        text = getString(Res.string.update_available_desktop, update.version),
                        link = update.link,
                    )
                }

                // the SDK update has its own badge next to the device / app selector
                is ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.ClientUpdate,
                ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.None -> null
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val desktopVersionAvailable = MutableStateFlow<VersionAvailableUiModel?>(null)

    val sdkUpdate: StateFlow<SdkUpdateUiModel?> = observeIsClientOnLastVersionUseCase()
        .map { it.toSdkUpdate() }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val state = desktopVersionAvailable
        .map { desktop -> VersionAvailableState(desktop = desktop) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            checkIsDesktopOnLastVersionUseCase(current = BuildConfig.APP_VERSION).alsoSuccess { newVersion ->
                desktopVersionAvailable.update { newVersion.toUiDesktop() }
            }
        }
    }

    private suspend fun IsLastVersionDomainModel.toUiDesktop(): VersionAvailableUiModel? = when (this) {
        is IsLastVersionDomainModel.NewVersionAvailable -> {
            VersionAvailableUiModel(
                version = this.name,
                link = this.link,
                title = getString(Res.string.new_desktop_version, name),
                subtitle = null,
            )
        }

        is IsLastVersionDomainModel.RunningLastVersion -> {
            null
        }
    }

    private fun IsLastVersionDomainModel.toSdkUpdate(): SdkUpdateUiModel? = when (this) {
        is IsLastVersionDomainModel.NewVersionAvailable -> SdkUpdateUiModel(
            currentVersion = oldVersion,
            latestVersion = name,
            dependency = "io.github.openflocon:flocon:$name",
            releaseNotesLink = link,
        )

        is IsLastVersionDomainModel.RunningLastVersion -> null
    }

    fun hideDesktopNewVersionDialog(uimodel: VersionAvailableUiModel) {
        desktopVersionAvailable.update { null }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            dismissDesktopVersionUseCase(uimodel.version)
        }
    }

    fun dismissSdkUpdate(uimodel: SdkUpdateUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            dismissClientVersionUseCase(uimodel.latestVersion)
        }
    }
}
