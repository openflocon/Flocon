package io.github.openflocon.flocondesktop.app.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.usecase.CheckIsDesktopOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.DismissClientVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveIsClientOnLastVersionUseCase
import io.github.openflocon.domain.versions.usecase.ObserveUpdateAvailableUseCase
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VersionCheckerViewModel(
    private val checkIsDesktopOnLastVersionUseCase: CheckIsDesktopOnLastVersionUseCase,
    private val dismissClientVersionUseCase: DismissClientVersionUseCase,
    private val observeIsClientOnLastVersionUseCase: ObserveIsClientOnLastVersionUseCase,
    private val observeUpdateAvailableUseCase: ObserveUpdateAvailableUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    /** A newer desktop app than the one running. */
    data class DesktopUpdateUiModel(
        val currentVersion: String,
        val latestVersion: String,
        val link: String,
    )

    /** A newer SDK than the one running in the selected device + app. */
    data class SdkUpdateUiModel(
        val currentVersion: String,
        val latestVersion: String,
        val dependency: String,
        val releaseNotesLink: String,
    )

    val desktopUpdate: StateFlow<DesktopUpdateUiModel?> = observeUpdateAvailableUseCase(BuildConfig.APP_VERSION)
        .map { update ->
            (update as? ObserveUpdateAvailableUseCase.UpdateAvailableDomainModel.DesktopUpdate)?.let {
                DesktopUpdateUiModel(
                    currentVersion = BuildConfig.APP_VERSION,
                    latestVersion = it.version,
                    link = it.link,
                )
            }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val sdkUpdate: StateFlow<SdkUpdateUiModel?> = observeIsClientOnLastVersionUseCase()
        .map { it.toSdkUpdate() }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    init {
        // fetches the latest released version: the desktop and SDK updates above are both derived from it
        viewModelScope.launch(dispatcherProvider.viewModel) {
            checkIsDesktopOnLastVersionUseCase(current = BuildConfig.APP_VERSION)
        }
    }

    private fun IsLastVersionDomainModel.toSdkUpdate(): SdkUpdateUiModel? = when (this) {
        is IsLastVersionDomainModel.NewVersionAvailable -> SdkUpdateUiModel(
            currentVersion = oldVersion,
            latestVersion = name,
            dependency = sdk.dependency(version = name),
            releaseNotesLink = link,
        )

        is IsLastVersionDomainModel.RunningLastVersion -> null
    }

    /** The line to put in the project of the app to use [version], in the syntax of the package manager of the SDK. */
    private fun FloconSdk?.dependency(version: String): String = when (this) {
        FloconSdk.SWIFT -> ".package(url: \"https://github.com/${FloconSdk.SWIFT.repository}.git\", from: \"$version\")"
        FloconSdk.FLUTTER -> "flocon: ^$version"
        FloconSdk.KOTLIN, null -> "io.github.openflocon:flocon:$version"
    }

    fun dismissSdkUpdate(uimodel: SdkUpdateUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            dismissClientVersionUseCase(uimodel.latestVersion)
        }
    }
}
