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

    // the one place owning the "at most once per launch" rule, whatever the event
    private val sentThisLaunch: MutableSet<String> = ConcurrentHashMap.newKeySet()

    val state: StateFlow<TelemetryStateDomainModel> = observeTelemetryStateUseCase()

    fun setLevel(level: TelemetryLevelDomainModel) {
        setTelemetryLevelUseCase(level)
    }

    fun track(event: String, properties: Map<String, Any> = emptyMap()) {
        trackTelemetryEventUseCase(event, properties)
    }

    /** A screen was opened: at most once per screen per launch (they open very often). */
    fun screenViewed(screen: TelemetryScreen) = screenViewed(screen.wireName)

    /** Same for a menu entry, by its menu id (`Network`, `Database`...). */
    fun screenViewed(screen: String) {
        if (!firstTimeThisLaunch("screen_viewed:$screen")) return
        track(TelemetryEvents.SCREEN_VIEWED, mapOf("screen" to screen))
    }

    /** The empty state of a documentation page was displayed: at most once per page per launch. */
    fun emptyStateShown(screen: String) {
        if (!firstTimeThisLaunch("screen_empty_shown:$screen")) return
        track(TelemetryEvents.SCREEN_EMPTY_SHOWN, mapOf("screen" to screen))
    }

    /** An MCP tool was called: at most once per tool per launch. [tool] is a fixed name, [client] a bounded category. */
    fun mcpUsed(tool: String, client: String) {
        if (!firstTimeThisLaunch("mcp_used:$tool")) return
        track(TelemetryEvents.MCP_USED, mapOf("tool" to tool, "client" to client))
    }

    /**
     * An action was used. [value] is only sent when it is a [Boolean] or an [Int].
     * [screen] overrides the screen of the [feature] (documentation pages for [TelemetryFeature.HelpOpened]).
     * [source] is a bounded value (see `HelpSource`).
     * A feature flagged [TelemetryFeature.oncePerLaunch] is sent at most once per launch.
     */
    fun featureUsed(
        feature: TelemetryFeature,
        value: Any? = null,
        screen: String? = null,
        source: String? = null,
        enabledMocksCount: Int? = null,
    ) {
        if (feature.oncePerLaunch && !firstTimeThisLaunch("feature_used:${feature.wireName}")) return
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

    /** True the first time [key] is seen. Nothing is consumed while nothing can be sent (before the user opted in). */
    private fun firstTimeThisLaunch(key: String): Boolean {
        if (!canSend()) return false
        return sentThisLaunch.add(key)
    }

    private fun canSend(): Boolean = state.value.canSend
}
