package io.github.openflocon.flocondesktop.app

import io.github.openflocon.domain.screen.usecase.ObserveRequestedDesktopScreenUseCase
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * Lets a feature ask the app to display another screen of the left menu,
 * so the menu selection follows (a feature can only navigate on its own routes).
 * The MCP asks the same way: its request waits until the window is able to apply it.
 */
class MenuNavigator(
    observeRequestedDesktopScreenUseCase: ObserveRequestedDesktopScreenUseCase,
) {
    private val featureRequests = MutableSharedFlow<SubScreen>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val requests: Flow<SubScreen> = merge(
        featureRequests,
        observeRequestedDesktopScreenUseCase().map { it.toSubScreen() },
    )

    fun open(screen: SubScreen) {
        featureRequests.tryEmit(screen)
    }
}
