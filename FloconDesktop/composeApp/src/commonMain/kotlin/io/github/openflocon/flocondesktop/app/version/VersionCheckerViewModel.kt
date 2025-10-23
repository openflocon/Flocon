package io.github.openflocon.flocondesktop.app.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.versions.model.IsLastVersionDomainModel
import io.github.openflocon.domain.versions.usecase.CheckIsLastVersionUseCase
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VersionCheckerViewModel(
    private val checkIsLastVersionUseCase: CheckIsLastVersionUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    data class VersionAvailableUiModel(
        val version: String,
        val link: String,
    )

    private val _versionAvailable = MutableStateFlow<VersionAvailableUiModel?>(null)
    val versionAvailable = _versionAvailable.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            checkIsLastVersionUseCase(current = BuildConfig.APP_VERSION).alsoSuccess { newVersion ->
                when(newVersion) {
                    is IsLastVersionDomainModel.NewVersionAvailable -> {
                        _versionAvailable.update {
                            VersionAvailableUiModel(
                                version = newVersion.name,
                                link = newVersion.link,
                            )
                        }
                    }
                    is IsLastVersionDomainModel.RunningLastVersion -> {
                        // no op
                    }
                }
            }
        }
    }

    fun hideNewVersionDialog() {
        _versionAvailable.update { null }
    }
}
