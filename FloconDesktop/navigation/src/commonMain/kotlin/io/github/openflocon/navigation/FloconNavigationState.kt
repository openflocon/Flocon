package io.github.openflocon.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

interface FloconNavigationState<T : Any> {
    val stack: SnapshotStateList<T>

    fun navigate(route: T)

    fun back(count: Int = 1)
}

class MainFloconNavigationState(initialScreen: FloconRoute = LoadingRoute) : FloconNavigationState<FloconRoute> {

    private val _stack = mutableStateListOf(initialScreen)
    override val stack: SnapshotStateList<FloconRoute> = _stack

    override fun navigate(route: FloconRoute) {
        _stack.add(route)
    }

    override fun back(count: Int) {
        repeat(count) { _stack.removeLast() }
    }

}
