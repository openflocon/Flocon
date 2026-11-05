package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The text typed in the tag field of the logs card.
 * The field reads [text], a Compose state written synchronously: it must never wait for an asynchronous pipeline,
 * or the field gets an old text back and the cursor jumps. The suggestions follow [query] and never feed the field.
 */
internal class TimelineLogTagInput {
    private var value by mutableStateOf("")
    private val mirror = MutableStateFlow("")

    val text: State<String> = object : State<String> {
        override val value: String get() = this@TimelineLogTagInput.value
    }

    // the same text for the flows that narrow the suggestions
    val query: StateFlow<String> get() = mirror

    fun set(text: String) {
        value = text
        mirror.value = text
    }

    fun clear() = set("")
}
