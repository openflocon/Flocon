package io.github.openflocon.flocon

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object Flocon : FloconApp() {
    private val _isInitialized = MutableStateFlow(false)
    actual override val isInitialized: StateFlow<Boolean> = _isInitialized
    
    // Android-specific initialize method that accepts Context
    // This is a no-op implementation
    @Suppress("UNUSED_PARAMETER")
    fun initialize(context: Context) {
        initialize()
    }
}

