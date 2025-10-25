package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object Flocon : FloconApp() {
    private val _isInitialized = MutableStateFlow(false)
    actual override val isInitialized: StateFlow<Boolean> = _isInitialized

    fun initialize() {}
}

