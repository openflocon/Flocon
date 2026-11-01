package io.github.openflocon.flocon.client

import io.github.openflocon.flocon.FloconConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * The state of each [FloconLink], and the one published to the app (`Flocon.connectionState`).
 *
 * An object, because the state can be observed before Flocon is started.
 */
internal object FloconConnectionStates {

    private val mutableState = MutableStateFlow<FloconConnectionState>(FloconConnectionState.Idle)
    private val linkStates = MutableStateFlow<Map<FloconLink, FloconConnectionState>>(emptyMap())

    /** Connected as soon as one address is, else the most advanced state of the addresses. */
    val state: StateFlow<FloconConnectionState> get() = mutableState

    fun set(link: FloconLink, state: FloconConnectionState) {
        publish(linkStates.updateAndGet { it + (link to state) })
    }

    fun clear(link: FloconLink) {
        publish(linkStates.updateAndGet { it - link })
    }

    private fun publish(states: Map<FloconLink, FloconConnectionState>) {
        val all = states.values
        mutableState.value = all.firstOrNull { it is FloconConnectionState.Connected }
            ?: all.firstOrNull { it is FloconConnectionState.Connecting }
            ?: all.firstOrNull { it is FloconConnectionState.Failed }
            ?: all.firstOrNull()
            ?: FloconConnectionState.Idle
    }
}
