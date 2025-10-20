package io.github.openflocon.flocon

import kotlinx.coroutines.flow.StateFlow

expect object Flocon : FloconApp {
    override val isInitialized: StateFlow<Boolean>
}