package io.github.openflocon.flocon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object Flocon : FloconApp() {
    private val _isInitialized = MutableStateFlow(false)
    actual override val isInitialized: StateFlow<Boolean> = _isInitialized
    
    init {
        // iOS implementation using Ktor
        // Simplified version - can be extended later
        super.initialize()
        _isInitialized.value = true
    }
}

