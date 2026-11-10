package io.github.openflocon.flocondesktop.features.webview

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface WebViewRoutes : FloconRoute {

    @Serializable
    data object Main : WebViewRoutes
}

fun EntryProviderScope<FloconRoute>.webViewRoutes() {
    entry<WebViewRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        WebViewScreen()
    }
}
