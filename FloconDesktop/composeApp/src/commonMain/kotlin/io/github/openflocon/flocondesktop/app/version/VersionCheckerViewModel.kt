package io.github.openflocon.flocondesktop.app.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.new_client_version
import flocondesktop.composeapp.generated.resources.new_client_version_desc
import flocondesktop.composeapp.generated.resources.new_desktop_version
import flocondesktop.composeapp.generated.resources.update_available_client
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
import kotlinx.coroutines.flow.combine
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
        val client: VersionAvailableUiModel?,
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
                is ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.ClientUpdate -> {
                    UpdateChipUiModel(
                        text = getString(Res.string.update_available_client, update.version),
                        link = update.link,
                    )
                }
                ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.None -> null
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val hiddenClientDialogs = MutableStateFlow<Set<VersionAvailableUiModel>>(emptySet())
    private val desktopVersionAvailable = MutableStateFlow<VersionAvailableUiModel?>(null)

    private val clientVersionAvailable = observeIsClientOnLastVersionUseCase()
        .map {
            it.toUiClient()
        }.combine(hiddenClientDialogs) { uimodel, hidden ->
            uimodel.takeIf { it !in hidden }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val state = combine(
        desktopVersionAvailable,
        clientVersionAvailable,
    ) { desktop, client ->
        VersionAvailableState(
            desktop = desktop,
            client = client,
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
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

    private suspend fun IsLastVersionDomainModel.toUiClient(): VersionAvailableUiModel? = when (this) {
        is IsLastVersionDomainModel.NewVersionAvailable -> {
            VersionAvailableUiModel(
                version = this.name,
                link = this.link,
                title = getString(Res.string.new_client_version, name),
                subtitle = getString(Res.string.new_client_version_desc, this.oldVersion),
            )
        }

        is IsLastVersionDomainModel.RunningLastVersion -> {
            null
        }
    }

    fun hideDesktopNewVersionDialog(uimodel: VersionAvailableUiModel) {
        desktopVersionAvailable.update { null }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            dismissDesktopVersionUseCase(uimodel.version)
        }
    }

    fun hideClientNewVersionDialog(uimodel: VersionAvailableUiModel) {
        hiddenClientDialogs.update { it + uimodel }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            dismissClientVersionUseCase(uimodel.version)
        }
    }
}
