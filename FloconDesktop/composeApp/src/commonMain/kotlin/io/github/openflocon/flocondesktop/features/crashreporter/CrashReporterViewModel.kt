package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.crashreporter.usecase.ObserveCrashReportsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CrashReporterViewModel(
    observeCrashReportsUseCase: ObserveCrashReportsUseCase
) : ViewModel() {

    val crashReports: StateFlow<List<CrashReportDomainModel>> = observeCrashReportsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
