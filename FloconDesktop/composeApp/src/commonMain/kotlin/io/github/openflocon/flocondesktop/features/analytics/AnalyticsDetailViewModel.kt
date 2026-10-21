package io.github.openflocon.flocondesktop.features.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.analytics.usecase.ObserveAnalyticsByIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.ui.detail.toCopyableText
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.analytics.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AnalyticsDetailViewModel(
    id: String,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeAnalyticsByIdUseCase: ObserveAnalyticsByIdUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val uiState = combine(
        observeAnalyticsByIdUseCase(id).filterNotNull(),
        observeCurrentDeviceIdAndPackageNameUseCase()
    ) { analytics, current ->
        analytics.mapToDetailUi(current)
    }
        .stateInWhileSubscribed<AnalyticsDetailUiModel?>(default = null)

    fun onCopyProperties() {
        val properties = uiState.value?.properties?.takeIf { it.isNotEmpty() } ?: return
        viewModelScope.launch {
            copyToClipboard(properties.map { it.name to it.value }.toCopyableText())
            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
        }
    }
}
