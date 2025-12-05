package io.github.openflocon.flocondesktop.device

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import kotlinx.serialization.Serializable

sealed interface DeviceRoutes : FloconRoute {

    @Serializable
    data class Detail(val id: DeviceId) : DeviceRoutes

}

fun EntryProviderScope<FloconRoute>.deviceRoutes() {
    entry<DeviceRoutes.Detail>(
        metadata = WindowSceneStrategy.window()
    ) {
        DeviceScreen(
            deviceId = it.id
        )
    }
}
