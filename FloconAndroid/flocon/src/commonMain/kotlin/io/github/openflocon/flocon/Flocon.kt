package io.github.openflocon.flocon

import kotlinx.coroutines.flow.StateFlow

// Common expect declaration for Flocon
// Android implementation will have full functionality with OkHttp
// JVM and iOS implementations will have simplified functionality with Ktor
expect object Flocon : FloconApp {
    override val isInitialized: StateFlow<Boolean>
}

