package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow

object Flocon : FloconApp() {
    override val isInitialized = MutableStateFlow(false)
}