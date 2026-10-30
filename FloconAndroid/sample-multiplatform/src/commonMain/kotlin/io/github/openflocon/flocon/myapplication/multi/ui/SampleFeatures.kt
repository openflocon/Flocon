package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.runtime.mutableStateMapOf
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.accessibility.accessibilityPlugin
import io.github.openflocon.flocon.analytics.analyticsPlugin
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.analyticsProperty
import io.github.openflocon.flocon.deeplinks.deeplinksPlugin
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.dashboard.user.userFlow
import io.github.openflocon.flocon.myapplication.multi.featureflags.initializeFeatureFlags
import io.github.openflocon.flocon.tables.dsl.table
import io.github.openflocon.flocon.tables.tablePlugin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import kotlin.time.TimeSource

/**
 * Every feature of the SDK that can be triggered from the sample app. Each one is displayed on the
 * screen of its plugin ([screen]).
 *
 * @param platforms where the sample can run it. Keep it in line with the registrations of each
 *   platform entry point ([SampleFeatures.on] / [SampleFeatures.unavailable]).
 * @param crashes the action is expected to crash the app, so its failure is not caught.
 */
enum class SampleFeature(
    val screen: SampleScreen,
    val title: String,
    val description: String,
    val buttonLabel: String = "Run",
    val platforms: Set<SamplePlatform> = SamplePlatform.All,
    val crashes: Boolean = false,
) {
    KtorGet(SampleScreen.Network, "Ktor GET", "Simple JSON request"),
    KtorPost(SampleScreen.Network, "Ktor POST", "JSON body and response"),
    KtorNotFound(SampleScreen.Network, "Ktor 404", "A failing request"),
    OkHttp(SampleScreen.Network, "OkHttp", "Interceptor on an OkHttp client", platforms = SamplePlatform.AndroidOnly),
    OkHttpGzip(SampleScreen.Network, "OkHttp gzip", "Compressed response body", platforms = SamplePlatform.AndroidOnly),
    WebSocket(SampleScreen.Network, "WebSocket", "Send a message on the open socket", buttonLabel = "Send", platforms = SamplePlatform.AndroidOnly),
    GraphQl(SampleScreen.Network, "GraphQL", "Apollo query on the GitHub API", platforms = SamplePlatform.AndroidOnly),
    Grpc(SampleScreen.Network, "gRPC", "SayHello unary call", platforms = SamplePlatform.AndroidOnly),

    DatabaseRoom3(SampleScreen.Database, "Dog, Room 3", "Insert a dog in the dogs database", buttonLabel = "Insert"),
    DatabaseRoom2(SampleScreen.Database, "Cat, Room 2", "Insert a cat in the Room 2 database", buttonLabel = "Insert", platforms = SamplePlatform.AndroidOnly),
    DatabaseInMemory(SampleScreen.Database, "Dog, in-memory", "Insert a dog in an in-memory Room 3 database", buttonLabel = "Insert", platforms = SamplePlatform.AndroidOnly),
    DatabaseFood(SampleScreen.Database, "Food, Room 3", "Insert food in the second database", buttonLabel = "Insert"),

    AnalyticsTable(SampleScreen.Analytics, "Table event", "One event in the table \"analytics\"", buttonLabel = "Send"),
    AnalyticsEvents(SampleScreen.Analytics, "Firebase-style events", "Two events from one sender", buttonLabel = "Send"),

    Accessibility(SampleScreen.Accessibility, "Announce", "Announces \"Item added to the cart\" to the screen reader", buttonLabel = "Announce"),

    WebViewConsole(SampleScreen.WebView, "Console messages", "console.log, info, warn and error from the page", buttonLabel = "Log", platforms = SamplePlatform.AndroidOnly),
    WebViewJsError(SampleScreen.WebView, "Uncaught JS error", "Throws a TypeError in the page", buttonLabel = "Throw", platforms = SamplePlatform.AndroidOnly),
    WebViewBridge(SampleScreen.WebView, "Bridge call", "The page calls the native object \"FloconSample\"", buttonLabel = "Call", platforms = SamplePlatform.AndroidOnly),
    WebViewNotFound(SampleScreen.WebView, "HTTP 404", "Loads a page that does not exist", buttonLabel = "Load", platforms = SamplePlatform.AndroidOnly),
    WebViewReload(SampleScreen.WebView, "Reload the sample page", "Back to the local page used by the other rows", buttonLabel = "Reload", platforms = SamplePlatform.AndroidOnly),

    FeatureFlags(SampleScreen.FeatureFlags, "Register flags", "Firebase and Statsig flags, with variants and payloads", buttonLabel = "Register"),

    DataStore(SampleScreen.Preferences, "DataStore", "Update the age stored in a DataStore", buttonLabel = "Update", platforms = SamplePlatform.AndroidOnly),

    Files(SampleScreen.Files, "Write a file", "Creates flocon_sample.txt, then browse it in Flocon", buttonLabel = "Write", platforms = SamplePlatform.NotWeb),

    Dashboard(SampleScreen.Dashboard, "Change user", "Updates the live \"User\" section", buttonLabel = "Change"),
    Table(SampleScreen.Dashboard, "Add table row", "Adds a row to the table \"active_sessions\"", buttonLabel = "Add"),

    Deeplink(SampleScreen.Deeplinks, "Register a deeplink", "Registers flocon://runtime/[id] after the install", buttonLabel = "Register"),

    CrashReporter(SampleScreen.Crash, "Crash the app", "Throws an IllegalStateException", buttonLabel = "Crash", platforms = SamplePlatform.NotWeb, crashes = true),
}

sealed interface SampleFeatureStatus {
    class Available(val action: suspend () -> Any?) : SampleFeatureStatus
    class Unavailable(val reason: String) : SampleFeatureStatus
}

/** What happened the last time a feature was triggered. */
sealed interface ActionResult {
    data object Running : ActionResult
    class Success(val millis: Long, val detail: String?) : ActionResult
    class Failure(val message: String) : ActionResult
}

/**
 * What the sample can do on the current platform. The shared features are registered here, each
 * platform entry point adds its own with [on] and explains the missing ones with [unavailable].
 * An action can return a String, it is displayed next to the duration.
 */
object SampleFeatures {
    /** Set by the entry point of each platform. */
    var platform: SamplePlatform? = null

    val platformName: String get() = platform?.displayName ?: "this platform"

    val results = mutableStateMapOf<SampleFeature, ActionResult>()

    private val actions = mutableMapOf<SampleFeature, suspend () -> Any?>()
    private val reasons = mutableMapOf<SampleFeature, String>()

    init {
        on(SampleFeature.KtorGet) { DummyHttpKtorCaller.callGet() }
        on(SampleFeature.KtorPost) { DummyHttpKtorCaller.callPost() }
        on(SampleFeature.KtorNotFound) { DummyHttpKtorCaller.callNotFound() }
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
        on(SampleFeature.Accessibility) { Flocon.accessibilityPlugin.announce("Item added to the cart") }
        on(SampleFeature.FeatureFlags) { initializeFeatureFlags() }
        on(SampleFeature.Deeplink) {
            Flocon.deeplinksPlugin.register {
                deeplink("flocon://runtime/[id]") {
                    label = "Registered after install"
                    "id" withAutoComplete listOf("1", "2", "3")
                }
            }
        }
        on(SampleFeature.CrashReporter) {
            throw IllegalStateException("my custom crash")
        }
    }

    fun on(feature: SampleFeature, action: suspend () -> Any?) {
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

    fun isAnyUnavailable(screen: SampleScreen): Boolean =
        SampleFeature.entries.any { it.screen == screen && status(it) is SampleFeatureStatus.Unavailable }

    /** Runs the action and records its result, shown on the row of the feature. */
    suspend fun run(feature: SampleFeature) {
        val action = (status(feature) as? SampleFeatureStatus.Available)?.action ?: return
        if (feature.crashes) {
            action()
            return
        }
        results[feature] = ActionResult.Running
        val start = TimeSource.Monotonic.markNow()
        results[feature] = try {
            val detail = action() as? String
            ActionResult.Success(start.elapsedNow().inWholeMilliseconds, detail)
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            ActionResult.Failure(t.message ?: t::class.simpleName ?: "Unknown error")
        }
    }
}
