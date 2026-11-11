package io.github.openflocon.flocondesktop.common.ui.featuredoc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalUriHandler

/** Where a help link was clicked. [wireName] is the bounded value sent as the `source` of telemetry. */
enum class HelpSource(val wireName: String) {
    /** (i) button at the top right of a page. */
    TopBar("top_bar"),

    /** "How to set up" button of an empty page. */
    EmptyState("empty_state"),

    /** "Getting started" link of the "no device connected" card. */
    EmptyStateNoDevice("empty_state_no_device"),
}

/**
 * Opens a documentation page. Shared design composables use it instead of the URI handler, so the app shell
 * decides what happens on a click (opening the URL, usage analytics) without any repository reaching them.
 *
 * @param doc the documented page, `null` for the getting started guide ([FeatureDoc.SETUP_URL])
 */
fun interface FeatureDocOpener {
    fun open(doc: FeatureDoc?, url: String, source: HelpSource)
}

/** Name sent as the `screen` of a help click: the documented page, `Setup` for the getting started guide. */
val FeatureDoc?.helpScreenName: String get() = this?.name ?: "Setup"

/** Provided by the app shell. Read it with [rememberFeatureDocOpener]. */
val LocalFeatureDocOpener = staticCompositionLocalOf<FeatureDocOpener?> { null }

/**
 * The opener provided by the app shell. Without one (previews, tests) it only opens the URL.
 */
@Composable
fun rememberFeatureDocOpener(): FeatureDocOpener {
    val provided = LocalFeatureDocOpener.current
    val uriHandler = LocalUriHandler.current
    return provided ?: remember(uriHandler) {
        FeatureDocOpener { _, url, _ -> runCatching { uriHandler.openUri(url) } }
    }
}
