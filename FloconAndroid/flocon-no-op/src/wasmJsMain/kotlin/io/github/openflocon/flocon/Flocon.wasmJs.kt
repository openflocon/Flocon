package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object Flocon : FloconApp {
    override val isInitialized: StateFlow<Boolean> = MutableStateFlow(false)
}
