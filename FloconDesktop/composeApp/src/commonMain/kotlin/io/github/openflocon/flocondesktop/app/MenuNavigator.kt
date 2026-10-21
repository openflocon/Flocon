package io.github.openflocon.flocondesktop.app

import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Lets a feature ask the app to display another screen of the left menu,
 * so the menu selection follows (a feature can only navigate on its own routes).
 */
class MenuNavigator {
    private val _requests = MutableSharedFlow<SubScreen>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val requests: SharedFlow<SubScreen> = _requests.asSharedFlow()

    fun open(screen: SubScreen) {
        _requests.tryEmit(screen)
    }
}
