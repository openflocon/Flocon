package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsByIdUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsUseCase
import io.github.openflocon.flocondesktop.features.crashreporter.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.crashreporter.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CrashReporterViewModel(
    observeCrashReportsUseCase: ObserveCrashReportsUseCase,
    private val observeCrashReportsByIdUseCase: ObserveCrashReportsByIdUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val crashReports: StateFlow<List<CrashReporterUiModel>> = observeCrashReportsUseCase()
        .map { list -> list.map { it.mapToUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedId = MutableStateFlow<String?>(null)
    val selected: StateFlow<CrashReporterSelectedUiModel?> = selectedId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                observeCrashReportsByIdUseCase(id)
            }
        }.map {
            it?.mapToDetailUi()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    fun onAction(action: CrashReporterAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is CrashReporterAction.Select -> {
                    selectedId.update { action.crashId }
                }
            }
        }
    }
}
