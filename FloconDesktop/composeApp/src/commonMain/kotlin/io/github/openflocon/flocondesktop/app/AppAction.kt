package io.github.openflocon.flocondesktop.app

import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

internal sealed interface AppAction {

    data class SelectMenu(val menu: SubScreen) : AppAction

    data class DeleteApp(val app: DeviceAppUiModel) : AppAction

    data class DeleteDevice(val device: DeviceItemUiModel) : AppAction

    data class SelectApp(val app: DeviceAppUiModel) : AppAction

    data class SelectDevice(val device: DeviceItemUiModel) : AppAction

    data object Record : AppAction

    data object Restart : AppAction

    data object Screenshoot : AppAction

}
