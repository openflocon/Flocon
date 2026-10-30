package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

/**
 * The WebView shown on the screen of the sample, set by the platform entry point that supports it
 * (a WebView cannot be created from the common code).
 */
object SampleWebViewHost {
    var content: (@Composable (Modifier) -> Unit)? = null
}

@Composable
fun WebViewScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.WebView, onBack) {
    val webView = SampleWebViewHost.content
    if (webView != null) {
        Card {
            webView(Modifier.fillMaxWidth().height(220.dp))
        }
    } else {
        InfoCard(text = "No WebView on ${SampleFeatures.platformName}.")
    }

    ActionGroup(
        "Page",
        listOf(
            SampleFeature.WebViewConsole,
            SampleFeature.WebViewJsError,
            SampleFeature.WebViewBridge,
        ),
    )
    ActionGroup(
        "Navigation",
        listOf(
            SampleFeature.WebViewNotFound,
            SampleFeature.WebViewReload,
        ),
    )
    InfoCard(
        text = "Open the WebViews screen of the desktop app: the console, the page events (load, HTTP errors) and the bridge calls appear there.",
        tint = SampleTheme.colors.inkMuted,
    )
    PlatformNote(SampleScreen.WebView)
}
