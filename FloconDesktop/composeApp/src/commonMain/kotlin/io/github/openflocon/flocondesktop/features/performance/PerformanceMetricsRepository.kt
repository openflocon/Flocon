package io.github.openflocon.flocondesktop.features.performance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PerformanceMetricsRepository {
    private val _metrics = MutableStateFlow<List<MetricEventUiModel>>(emptyList())
    val metrics = _metrics.asStateFlow()

    fun addMetric(metric: MetricEventUiModel) {
        _metrics.update { listOf(metric) + it }
    }

    fun clear() {
        _metrics.value = emptyList()
    }
    
    fun getMetrics() = _metrics.value
}
