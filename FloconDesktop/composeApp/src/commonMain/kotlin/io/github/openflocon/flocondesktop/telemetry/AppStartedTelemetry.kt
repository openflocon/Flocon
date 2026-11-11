package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.settings.usecase.ObserveMascotEnabledUseCase
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Sends [TelemetryEvents.APP_STARTED] with a snapshot of the settings, to know the share of launches using a feature.
 * Only booleans, counts and enum wire names. A value that can't be read within [readTimeout] is left out.
 *
 * The mocks and bad network values are the ones of the current device and app: they are read once an app is selected,
 * and left out when none is within [readTimeout] (the first launch, or no app connected yet).
 */
internal class AppStartedTelemetry(
    private val telemetryManager: TelemetryManager,
    private val timelineSettings: suspend () -> TimelineSettingsDomainModel,
    private val mascotEnabled: suspend () -> Boolean,
    private val enabledMocksCount: suspend () -> Int,
    private val badNetworkEnabled: suspend () -> Boolean,
    private val readTimeout: Duration = 3.seconds,
) {

    private val sent = AtomicBoolean(false)

    /** Once per launch, whichever [TelemetryViewModel] instance asks (the Settings screen creates its own). */
    suspend fun send() {
        // wait for the user's answer: nothing is consumed while nothing can be sent
        telemetryManager.state.first { it.canSend }
        if (!sent.compareAndSet(false, true)) return
        val properties = mutableMapOf<String, Any>()

        // independent reads: the ones waiting for a device must not delay the others
        coroutineScope {
            val timeline = async { read(timelineSettings) }
            val mascot = async { read(mascotEnabled) }
            val mocks = async { read(enabledMocksCount) }
            val badNetwork = async { read(badNetworkEnabled) }

            timeline.await()?.let {
                properties["timeline_view"] = if (it.graph) "graph" else "list"
                properties["timeline_auto_scroll"] = it.autoScroll
                properties["timeline_screenshots"] = it.screenshots
                properties["timeline_merge_plugins"] = it.mergePlugins
            }
            mascot.await()?.let { properties["mascot_enabled"] = it }
            mocks.await()?.let { properties["enabled_mocks_count"] = it }
            badNetwork.await()?.let { properties["bad_network_enabled"] = it }
        }

        telemetryManager.track(TelemetryEvents.APP_STARTED, properties)
    }

    private suspend fun <T : Any> read(source: suspend () -> T): T? = try {
        withTimeoutOrNull(readTimeout) { source() }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}

internal fun appStartedTelemetry(
    telemetryManager: TelemetryManager,
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
    observeMascotEnabledUseCase: ObserveMascotEnabledUseCase,
    observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    observeNetworkMocksUseCase: ObserveNetworkMocksUseCase,
    observeAllNetworkBadQualitiesUseCase: ObserveAllNetworkBadQualitiesUseCase,
) = AppStartedTelemetry(
    telemetryManager = telemetryManager,
    timelineSettings = { observeTimelineSettingsUseCase().first() },
    mascotEnabled = { observeMascotEnabledUseCase().first() },
    enabledMocksCount = {
        observeCurrentDeviceIdAndPackageNameUseCase().filterNotNull().first()
        observeNetworkMocksUseCase().first().count { it.isEnabled }
    },
    badNetworkEnabled = {
        observeCurrentDeviceIdAndPackageNameUseCase().filterNotNull().first()
        observeAllNetworkBadQualitiesUseCase().first().any { it.isEnabled }
    },
)
