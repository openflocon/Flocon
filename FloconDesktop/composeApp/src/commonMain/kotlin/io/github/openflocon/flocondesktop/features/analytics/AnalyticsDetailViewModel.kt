package io.github.openflocon.flocondesktop.features.analytics

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.analytics.usecase.ObserveAnalyticsByIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.analytics.mapper.mapToDetailUi
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class AnalyticsDetailViewModel(
    id: String,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeAnalyticsByIdUseCase: ObserveAnalyticsByIdUseCase
) : ViewModel() {

    val uiState = combine(
        observeAnalyticsByIdUseCase(id).filterNotNull(),
        observeCurrentDeviceIdAndPackageNameUseCase()
    ) { analytics, current ->
        analytics.mapToDetailUi(current)
    }
        .stateInWhileSubscribed(
            default = AnalyticsDetailUiModel(
                id = id,
                dateFormatted = "",
                eventName = "",
                isFromOldAppInstance = false,
                properties = emptyList()
            )
        )

}
