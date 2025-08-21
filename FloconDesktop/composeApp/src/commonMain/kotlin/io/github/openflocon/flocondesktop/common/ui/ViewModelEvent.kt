package io.github.openflocon.flocondesktop.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface ViewModelEvent<E> {

    val events: Flow<E>

    context(vm: ViewModel)
    fun sendEvents(vararg event: E)

    interface Event
}

class ViewModelEventImpl<E : ViewModelEvent.Event> : ViewModelEvent<E> {

    private val _events = Channel<E>()
    override val events: Flow<E> = _events.receiveAsFlow()

    context(vm: ViewModel)
    override fun sendEvents(vararg event: E) {
        vm.viewModelScope.launch {
            event.forEach { _events.send(it) }
        }
    }
}
