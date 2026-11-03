package io.github.openflocon.flocondesktop.window

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.CurrentDeviceIdProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSummaryUseCase
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineUseCase
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.features.timeline.TimelineFilterPanelViewModel
import io.github.openflocon.flocondesktop.features.timeline.TimelineFilterStore
import io.github.openflocon.flocondesktop.features.timeline.TimelineRoutes
import io.github.openflocon.flocondesktop.features.timeline.TimelineViewModel
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** The scope of a window opened for a device: the device is the current one for everything resolved in it. */
val DeviceWindowScope = named("deviceWindow")

/** The device a [DeviceWindowScope] is pinned to, declared when the scope is created. */
data class DeviceWindowTarget(val deviceId: DeviceId)

private class PinnedCurrentDeviceIdProvider(
    private val target: DeviceWindowTarget,
) : CurrentDeviceIdProvider {
    override fun observe(): Flow<DeviceId?> = flowOf(target.deviceId)
}

/**
 * The use cases are factories of the root: they only see the root, so everything between "the current device"
 * and the timeline is declared again here, on the pinned device. The other features keep following the main window.
 */
internal val deviceWindowModule = module {
    scope(DeviceWindowScope) {
        scoped<CurrentDeviceIdProvider> { PinnedCurrentDeviceIdProvider(target = get()) }
        scoped { ObserveCurrentDeviceIdAndPackageNameUseCase(currentDeviceIdProvider = get(), devicesRepository = get<DevicesRepository>()) }
        scoped { ObserveIsCurrentDeviceIAndAppIsActiveUseCase(get(), get<DevicesRepository>()) }
        scoped { ObserveTimelineUseCase(repository = get(), observeCurrentDeviceIdAndPackageNameUseCase = get()) }
        scoped { ObserveTimelineSummaryUseCase(repository = get(), observeCurrentDeviceIdAndPackageNameUseCase = get()) }

        scoped { MainFloconNavigationState(TimelineRoutes.Main) }
        scopedOf(::TimelineFilterStore)
        scopedOf(::NetworkItemOptionsDelegate)
        viewModelOf(::TimelineViewModel)
        viewModelOf(::TimelineFilterPanelViewModel)
    }
}
