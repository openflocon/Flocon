package io.github.openflocon.flocondesktop.app.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VersionCheckerViewModel(
    private val checkIsDesktopOnLastVersionUseCase: CheckIsDesktopOnLastVersionUseCase,
    private val observeIsClientOnLastVersionUseCase: ObserveIsClientOnLastVersionUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

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

    private val _hiddenClientDialogs = MutableStateFlow<Set<VersionAvailableUiModel>>(emptySet())
    private val _desktopVersionAvailable = MutableStateFlow<VersionAvailableUiModel?>(null)
    private val desktopVersionAvailable = _desktopVersionAvailable.asStateFlow()

    private val clientVersionAvailable = observeIsClientOnLastVersionUseCase()
        .map {
            it.toUiClient()
        }.combine(_hiddenClientDialogs) { uimodel, hidden ->
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
                _desktopVersionAvailable.update { newVersion.toUiDesktop() }
            }
        }
    }

    private fun IsLastVersionDomainModel.toUiDesktop(): VersionAvailableUiModel? {
        return when (this) {
            is IsLastVersionDomainModel.NewVersionAvailable -> {
                VersionAvailableUiModel(
                    version = this.name,
                    link = this.link,
                    title = "New destkop version available: ${name}",
                    subtitle = null,
                )
            }

            is IsLastVersionDomainModel.RunningLastVersion -> {
                null
            }
        }
    }

    private fun IsLastVersionDomainModel.toUiClient(): VersionAvailableUiModel? {
        return when (this) {
            is IsLastVersionDomainModel.NewVersionAvailable -> {
                VersionAvailableUiModel(
                    version = this.name,
                    link = this.link,
                    title = "New client version available: $name",
                    subtitle = "Don’t forget to update the app version\n(current: ${this.oldVersion})",
                )
            }

            is IsLastVersionDomainModel.RunningLastVersion -> {
                null
            }
        }
    }

    fun hideDesktopNewVersionDialog(uimodel: VersionAvailableUiModel) {
        _desktopVersionAvailable.update { null }
    }

    fun hideClientNewVersionDialog(uimodel: VersionAvailableUiModel) {
        _hiddenClientDialogs.update { it + uimodel }
    }
}
