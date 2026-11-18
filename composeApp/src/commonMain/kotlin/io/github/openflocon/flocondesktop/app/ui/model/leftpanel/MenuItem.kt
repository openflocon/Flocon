package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.domain.device.models.DevicePlugin
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

@Immutable
data class MenuItem(
    val screen: SubScreen,
    val icon: ImageVector,
    val isEnabled: Boolean,
    // the plugin that must be installed in the SDK for this item to be displayed, null: always displayed
    val requiredPlugin: DevicePlugin? = null,
    val isPinned: Boolean = false,
    val isPinnable: Boolean = true,
)
