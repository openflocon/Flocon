package io.github.openflocon.flocondesktop.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface ViewModelEvent<E> {

    val events: SharedFlow<E>

    fun ViewModel.sendEvents(vararg event: E)

    interface Event
}

class ViewModelEventImpl<E : ViewModelEvent.Event> : ViewModelEvent<E> {

    private val _events = MutableSharedFlow<E>()
    override val events: SharedFlow<E> = _events.asSharedFlow()

    override fun ViewModel.sendEvents(vararg event: E) {
        viewModelScope.launch {
            event.forEach { _events.emit(it) }
        }
    }
}
