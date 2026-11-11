package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryStateDomainModel
import io.github.openflocon.domain.telemetry.usecase.ObserveTelemetryStateUseCase
import io.github.openflocon.domain.telemetry.usecase.SetTelemetryLevelUseCase
import io.github.openflocon.domain.telemetry.usecase.TrackTelemetryEventUseCase
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Single entry point for usage analytics on Flocon Desktop.
 *
 * Only call [track] with feature-level information (screen names, version, OS).
 * Never pass anything coming from the inspected apps: URLs, payloads, package names, database content...
 */
class TelemetryManager(
    observeTelemetryStateUseCase: ObserveTelemetryStateUseCase,
    private val setTelemetryLevelUseCase: SetTelemetryLevelUseCase,
    private val trackTelemetryEventUseCase: TrackTelemetryEventUseCase,
) {

    private val screensSentThisLaunch: MutableSet<String> = ConcurrentHashMap.newKeySet()

    val state: StateFlow<TelemetryStateDomainModel> = observeTelemetryStateUseCase()

    fun setLevel(level: TelemetryLevelDomainModel) {
        setTelemetryLevelUseCase(level)
    }

    fun track(event: String, properties: Map<String, Any> = emptyMap()) {
        trackTelemetryEventUseCase(event, properties)
    }

    /** A sub-screen was opened. */
    fun screenViewed(screen: TelemetryScreen) {
        track(TelemetryEvents.SCREEN_VIEWED, mapOf("screen" to screen.wireName))
    }

    /** Like [screenViewed], but for a screen that opens very often (ex: a panel): at most once per launch. */
    fun trackScreenOncePerLaunch(screen: TelemetryScreen) {
        if (!canSend()) return // don't burn the "once" while nothing can be sent
        if (!screensSentThisLaunch.add(screen.wireName)) return
        screenViewed(screen)
    }

    /**
     * An action was used. [value] is only sent when it is a [Boolean] or an [Int].
     * [screen] overrides the screen of the [feature] (documentation pages for [TelemetryFeature.HelpOpened]).
     * [source] is a bounded value (see `HelpSource`).
     */
    fun featureUsed(
        feature: TelemetryFeature,
        value: Any? = null,
        screen: String? = null,
        source: String? = null,
        enabledMocksCount: Int? = null,
    ) {
        val properties = mutableMapOf<String, Any>(
            "feature" to feature.wireName,
            "screen" to (screen ?: feature.screen.wireName),
        )
        value.boundedOrNull()?.let { properties["value"] = it }
        source?.let { properties["source"] = it }
        enabledMocksCount?.let { properties["enabled_mocks_count"] = it }
        track(TelemetryEvents.FEATURE_USED, properties)
    }

    /** A setting was changed. [value] is only sent when it is a [Boolean], an [Int] or an enum wire name ([String]). */
    fun settingChanged(setting: TelemetrySetting, value: Any) {
        val properties = mutableMapOf<String, Any>(
            "setting" to setting.wireName,
            "screen" to setting.screen.wireName,
        )
        value.boundedOrNull()?.let { properties["value"] = it }
        track(TelemetryEvents.SETTING_CHANGED, properties)
    }

    private fun Any?.boundedOrNull(): Any? = when (this) {
        is Boolean, is Int, is String -> this
        else -> null
    }

    private fun canSend(): Boolean = state.value.canSend
}
