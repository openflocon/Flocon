package io.github.openflocon.flocondesktop.window

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.usecase.CurrentDeviceIdProvider
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOptionsDelegate
import io.github.openflocon.flocondesktop.features.timeline.TimelineFilterStore
import io.github.openflocon.flocondesktop.features.timeline.TimelineRoutes
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.dsl.scopedOf
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
    override suspend fun get(): DeviceId = target.deviceId
}

/**
 * What a window opened for a device does not share with the main window.
 *
 * The use cases, the delegates and the view models are factories of the root: resolved from the scope, they are built
 * with it, so they get what is declared here instead of the root one. Nothing to redeclare for them, they only read
 * the current device through [CurrentDeviceIdProvider].
 *
 * What is declared here is the state of a window, which the root keeps as a single:
 * - the current device,
 * - its navigation,
 * - the other state that is shared by the screens of a window.
 *
 * A single of the root is built once, with the root: it never sees the scope. A new single that depends on the
 * current device, or that holds the state of a window, has to be declared here as well.
 */
internal val deviceWindowModule = module {
    scope(DeviceWindowScope) {
        scoped<CurrentDeviceIdProvider> { PinnedCurrentDeviceIdProvider(target = get()) }
        scoped { MainFloconNavigationState(TimelineRoutes.Main) }
        scopedOf(::TimelineFilterStore)
        scopedOf(::NetworkItemOptionsDelegate)
    }
}
