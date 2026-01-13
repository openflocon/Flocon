package io.github.openflocon.flocondesktop.features.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.ByteFormatter
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.performance.usecase.FetchPerformanceMetricsUseCase
import io.github.openflocon.domain.performance.usecase.GetAdbDevicesUseCase
import io.github.openflocon.domain.performance.usecase.GetDeviceRefreshRateUseCase
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PerformanceViewModel(
    private val fetchPerformanceMetricsUseCase: FetchPerformanceMetricsUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getAdbDevicesUseCase: GetAdbDevicesUseCase,
    private val getDeviceRefreshRateUseCase: GetDeviceRefreshRateUseCase,
    private val navigationState: MainFloconNavigationState,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _devices = MutableStateFlow<List<String>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    private val _packageName = MutableStateFlow("")
    val packageName = _packageName.asStateFlow()

    private val _metrics = MutableStateFlow<List<MetricEventUiModel>>(emptyList())
    val metrics = _metrics.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring = _isMonitoring.asStateFlow()

    private var monitoringJob: Job? = null

    private var lastFrameCount: Int? = null
    private var lastFetchTime: Long? = null
    private var refreshRate: Double = 60.0

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val deviceList = getAdbDevicesUseCase()
            _devices.value = deviceList
            if (_selectedDevice.value == null && deviceList.isNotEmpty()) {
                _selectedDevice.value = deviceList.first()
            }
        }
        viewModelScope.launch(dispatcherProvider.viewModel) {
            getCurrentDeviceIdAndPackageNameUseCase()?.let { (_, packageName) ->
                _packageName.value = packageName
            }
        }
    }

    fun onDeviceSelected(deviceId: String) {
        _selectedDevice.value = deviceId
    }

    fun onPackageNameChanged(name: String) {
        _packageName.value = name
    }

    fun toggleMonitoring() {
        if (_isMonitoring.value) {
            stopMonitoring()
        } else {
            startMonitoring()
        }
    }

    private fun startMonitoring() {
        _isMonitoring.value = true
        monitoringJob = viewModelScope.launch(dispatcherProvider.viewModel) {
            val deviceSerial = _selectedDevice.value
            if (deviceSerial != null) {
                refreshRate = getDeviceRefreshRateUseCase(deviceSerial)
            }

            var tickCount = 0
            while (isActive) {
                val shouldCaptureScreenshot = tickCount % 3 == 0
                fetchMetrics(shouldCaptureScreenshot)
                delay(333)
                tickCount++
            }
        }
    }

    private fun stopMonitoring() {
        _isMonitoring.value = false
        monitoringJob?.cancel()
        monitoringJob = null
    }

    private suspend fun fetchMetrics(shouldCaptureScreenshot: Boolean = true) {
        val deviceSerial = _selectedDevice.value ?: return
        val pkg = _packageName.value

        val domainModel = fetchPerformanceMetricsUseCase(
            deviceSerial = deviceSerial,
            packageName = pkg,
            lastFrameCount = lastFrameCount,
            lastFetchTime = lastFetchTime,
            refreshRate = refreshRate,
            captureScreenshot = shouldCaptureScreenshot
        )

        lastFrameCount = domainModel.totalFrames
        lastFetchTime = domainModel.timestamp

        val event = MetricEventUiModel(
            timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(domainModel.timestamp),
                ZoneId.systemDefault()
            )
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
            ramMb = domainModel.ramMb?.let { ByteFormatter.formatBytes(it) },
            fps = if (domainModel.fps > 0) String.format("%.1f", domainModel.fps) else "0",
            jankPercentage = String.format("%.1f%%", domainModel.jankPercentage),
            battery = domainModel.battery,
            screenshotPath = domainModel.screenshotPath
        )

        _metrics.update { listOf(event) + it }
    }

    fun onEventClicked(event: MetricEventUiModel) {
        navigationState.navigate(PerformanceRoutes.Detail(event))
    }
}
