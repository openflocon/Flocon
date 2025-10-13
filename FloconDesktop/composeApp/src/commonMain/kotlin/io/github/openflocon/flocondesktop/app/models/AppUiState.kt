package io.github.openflocon.flocondesktop.app.models

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.previewLeftPannelState

@Immutable
internal data class AppUiState(
    val menuState: LeftPanelState // TODO Rename
)

private fun previewAppUiState() = AppUiState(
    menuState = previewLeftPannelState(SubScreen.Network)
)
