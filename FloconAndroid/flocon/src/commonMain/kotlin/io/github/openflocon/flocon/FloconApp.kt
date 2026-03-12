package io.github.openflocon.flocon

import kotlinx.coroutines.flow.StateFlow

abstract class FloconApp {
    lateinit var context: FloconContext

    companion object {
        var instance: FloconApp? = null
            private set
    }

    interface Client {

        @Throws(Throwable::class)
        suspend fun connect(
            onClosed: () -> Unit,
            onMessageReceived: (message: String) -> Unit
        )

        suspend fun disconnect()

    }

    open val client: Client? = null

    abstract val isInitialized: StateFlow<Boolean>

}