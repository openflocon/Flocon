package io.github.openflocon.library.designsystem.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

fun <T> MutableState<T>.asState(): State<T> = this
