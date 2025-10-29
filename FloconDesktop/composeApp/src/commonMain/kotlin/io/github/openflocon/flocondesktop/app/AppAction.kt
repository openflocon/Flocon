package io.github.openflocon.flocondesktop.app

import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

internal sealed interface AppAction {

    data class SelectMenu(val menu: SubScreen) : AppAction

}
