package io.github.openflocon.flocondesktop.app

import io.github.openflocon.flocondesktop.main.ui.model.SubScreen

internal sealed interface AppAction {

    data class SelectMenu(val screen: SubScreen) : AppAction

}
