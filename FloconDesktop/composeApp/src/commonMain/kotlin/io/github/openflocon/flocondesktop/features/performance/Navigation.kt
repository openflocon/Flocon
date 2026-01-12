package io.github.openflocon.flocondesktop.features.performance

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.features.performance.view.PerformanceDetailScreen
import io.github.openflocon.flocondesktop.features.performance.view.PerformanceScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.WindowRoute
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import kotlinx.serialization.Serializable

internal sealed interface PerformanceRoutes : FloconRoute {

    @Serializable
    data object Performance : PerformanceRoutes, WindowRoute {
        override val singleTopKey = "performance"
    }
 
    @Serializable
    data class Detail(val event: MetricEventUiModel) : PerformanceRoutes, WindowRoute {
        override val singleTopKey = null
    }
}

fun EntryProviderScope<FloconRoute>.performanceRoutes() {
    entry<PerformanceRoutes.Performance>(
        metadata = WindowSceneStrategy.window(
            size = DpSize(
                width = 1000.dp,
                height = 800.dp
            )
        )
    ) {
        PerformanceScreen()
    }
    entry<PerformanceRoutes.Detail>(
        metadata = WindowSceneStrategy.window(
            size = DpSize(
                width = 800.dp,
                height = 1000.dp
            )
        )
    ) {
        PerformanceDetailScreen(event = it.event)
    }
}
