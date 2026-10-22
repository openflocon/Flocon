package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup

@Composable
fun NetworkScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Network, onBack) {
    ActionGroup(
        "HTTP",
        listOf(
            SampleFeature.KtorGet,
            SampleFeature.KtorPost,
            SampleFeature.KtorNotFound,
            SampleFeature.OkHttp,
            SampleFeature.OkHttpGzip,
        ),
    )
    ActionGroup("Realtime & APIs", listOf(SampleFeature.WebSocket, SampleFeature.GraphQl, SampleFeature.Grpc))
    PlatformNote(SampleScreen.Network)
}
