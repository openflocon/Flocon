package io.github.openflocon.flocondesktop.features.performance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PerformanceDetailViewModel(
    initialEvent: MetricEventUiModel,
    private val repository: PerformanceMetricsRepository,
) : ViewModel() {

    private val _event = MutableStateFlow(initialEvent)
    val event = _event.asStateFlow()

    private val _hasNext = MutableStateFlow(false)
    val hasNext = _hasNext.asStateFlow()

    private val _hasPrevious = MutableStateFlow(false)
    val hasPrevious = _hasPrevious.asStateFlow()

    init {
        updateNavigationState()
    }

    fun onNext() {
        val metrics = repository.getMetrics()
        val currentIndex = metrics.indexOfFirst { it.timestamp == _event.value.timestamp }
        if (currentIndex > 0) {
            _event.value = metrics[currentIndex - 1]
            updateNavigationState()
        }
    }

    fun onPrevious() {
        val metrics = repository.getMetrics()
        val currentIndex = metrics.indexOfFirst { it.timestamp == _event.value.timestamp }
        if (currentIndex != -1 && currentIndex < metrics.size - 1) {
            _event.value = metrics[currentIndex + 1]
            updateNavigationState()
        }
    }

    private fun updateNavigationState() {
        val metrics = repository.getMetrics()
        val currentIndex = metrics.indexOfFirst { it.timestamp == _event.value.timestamp }
        _hasNext.value = currentIndex > 0
        _hasPrevious.value = currentIndex != -1 && currentIndex < metrics.size - 1
    }
}
