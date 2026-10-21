package io.github.openflocon.flocon.myapplication.multi.ui

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.analytics.analyticsPlugin
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.analyticsProperty
import io.github.openflocon.flocon.deeplinks.deeplinksPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.dashboard.user.userFlow
import io.github.openflocon.flocon.myapplication.multi.featureflags.initializeFeatureFlags
import io.github.openflocon.flocon.myapplication.multi.preferences.SamplePreferences
import io.github.openflocon.flocon.plugins.sharedprefs.preferencesPlugin
import io.github.openflocon.flocon.tables.dsl.table
import io.github.openflocon.flocon.tables.tablePlugin
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/**
 * Every feature of the SDK that can be triggered from the sample app, grouped by plugin.
 */
enum class SampleFeature(val group: String, val title: String) {
    KtorGet("Network", "Ktor GET"),
    KtorPost("Network", "Ktor POST"),
    OkHttp("Network", "OkHttp"),
    OkHttpGzip("Network", "OkHttp gzip"),
    WebSocket("Network", "WebSocket"),
    GraphQl("Network", "GraphQL"),
    Grpc("Network", "gRPC"),
    AnalyticsTable("Analytics", "Table event"),
    AnalyticsEvents("Analytics", "Analytics events"),
    Table("Tables", "Add table row"),
    Dashboard("Dashboard", "Change user"),
    FeatureFlags("Feature flags", "Register flags"),
    Deeplink("Deeplinks", "Register deeplink later"),
    DatabaseRoom3("Database", "Insert dog (Room 3)"),
    DatabaseRoom2("Database", "Insert cat (Room 2)"),
    DatabaseInMemory("Database", "Insert dog in-memory (Room 3)"),
    Preferences("Preferences", "Update custom preference"),
    DataStore("Preferences", "Update DataStore"),
    Files("Files", "Write a file"),
    CrashReporter("Crash reporter", "Crash"),
}

sealed interface SampleFeatureStatus {
    class Available(val action: suspend () -> Unit) : SampleFeatureStatus
    class Unavailable(val reason: String) : SampleFeatureStatus
}

/**
 * What the sample can do on the current platform. The shared features are registered here, each
 * platform entry point adds its own with [on] and explains the missing ones with [unavailable].
 * A feature without action is displayed as a gray button.
 */
object SampleFeatures {
    var platformName: String = "this platform"

    private val actions = mutableMapOf<SampleFeature, suspend () -> Unit>()
    private val reasons = mutableMapOf<SampleFeature, String>()

    init {
        on(SampleFeature.KtorGet) { DummyHttpKtorCaller.callGet() }
        on(SampleFeature.KtorPost) { DummyHttpKtorCaller.callPost() }
        on(SampleFeature.AnalyticsTable) {
            Flocon.analyticsPlugin.log(
                AnalyticsEvent(
                    eventName = "clicked user",
                    analyticsTableId = "analytics",
                    properties = listOf(
                        "userId" analyticsProperty "1024",
                        "username" analyticsProperty "florent",
                        "index" analyticsProperty "3",
                    ),
                )
            )
        }
        on(SampleFeature.AnalyticsEvents) {
            Flocon.analyticsPlugin.sender("firebase").apply {
                log(
                    "clicked user",
                    "userId" analyticsProperty "1024",
                    "username" analyticsProperty "florent",
                )
                log(
                    "opened profile",
                    "userId" analyticsProperty "2048",
                    "username" analyticsProperty "kevin",
                )
            }
        }
        on(SampleFeature.Table) {
            Flocon.tablePlugin.table("active_sessions") {
                column("user_id", Random.nextInt(1000).toString())
                column("name", "Flocon")
                column("status", "Active")
            }
        }
        on(SampleFeature.Dashboard) {
            userFlow.update { it.copy(userName = "flo-${Random.nextInt(100)}") }
        }
        on(SampleFeature.FeatureFlags) { initializeFeatureFlags() }
        on(SampleFeature.Deeplink) {
            Flocon.deeplinksPlugin.register {
                deeplink("flocon://runtime/[id]") {
                    label = "Registered after install"
                    "id" withAutoComplete listOf("1", "2", "3")
                }
            }
        }
        on(SampleFeature.Preferences) {
            SamplePreferences.counter++
        }
        on(SampleFeature.CrashReporter) {
            throw IllegalStateException("my custom crash")
        }
    }

    /** Must be called once Flocon is started. */
    fun registerPreferences() {
        Flocon.preferencesPlugin.register(SamplePreferences)
    }

    fun on(feature: SampleFeature, action: suspend () -> Unit) {
        actions[feature] = action
        reasons.remove(feature)
    }

    fun unavailable(feature: SampleFeature, reason: String) {
        actions.remove(feature)
        reasons[feature] = reason
    }

    fun status(feature: SampleFeature): SampleFeatureStatus {
        val action = actions[feature]
        return if (action != null) {
            SampleFeatureStatus.Available(action)
        } else {
            SampleFeatureStatus.Unavailable(reasons[feature] ?: "Not available on $platformName")
        }
    }
}
