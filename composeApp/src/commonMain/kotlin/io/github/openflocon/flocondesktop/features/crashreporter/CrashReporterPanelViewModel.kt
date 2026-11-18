package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.crashreporter.usecase.DeleteCrashReportUseCase
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsByIdUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.crashreporter.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// the detail of one crash, displayed in a panel (ex: from the timeline)
internal class CrashReporterPanelViewModel(
    private val crashId: String,
    observeCrashReportsByIdUseCase: ObserveCrashReportsByIdUseCase,
    private val deleteCrashReportUseCase: DeleteCrashReportUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val navigationState: MainFloconNavigationState,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    // null while loading, or when the crash does not exist anymore
    val crash: StateFlow<CrashReporterSelectedUiModel?> = observeCrashReportsByIdUseCase(crashId)
        .map { it?.mapToDetailUi() }
        .stateInWhileSubscribed(default = null)

    fun copy(crash: CrashReporterSelectedUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            copyToClipboard(crash.stackTrace)
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }

    fun delete() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteCrashReportUseCase(crashId)
            navigationState.remove(CrashReporterRoutes.Panel(crashId))
        }
    }
}
