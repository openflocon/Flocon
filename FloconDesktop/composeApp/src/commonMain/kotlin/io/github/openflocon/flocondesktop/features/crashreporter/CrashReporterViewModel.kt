package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.crashreporter.usecase.ClearAllCrashReportUseCase
import io.github.openflocon.domain.crashreporter.usecase.DeleteCrashReportUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsByIdUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.crashreporter.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.crashreporter.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.Flow
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
import org.jetbrains.compose.resources.getString

internal class CrashReporterViewModel(
    observeCrashReportsUseCase: ObserveCrashReportsUseCase,
    private val observeCrashReportsByIdUseCase: ObserveCrashReportsByIdUseCase,
    private val deleteCrashReportUseCase: DeleteCrashReportUseCase,
    private val clearAllCrashReportUseCase: ClearAllCrashReportUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val crashReports: Flow<PagingData<CrashReporterUiModel>> = observeCrashReportsUseCase()
        .map { pagingData -> pagingData.map { it.mapToUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .cachedIn(viewModelScope)

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

                is CrashReporterAction.Clean -> {
                    clearAllCrashReportUseCase()
                }
                is CrashReporterAction.Copy -> {
                    copyToClipboard(action.crash.stackTrace)
                    feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
                }
                is CrashReporterAction.Delete -> deleteCrashReportUseCase(action.crashId)
            }
        }
    }
}
