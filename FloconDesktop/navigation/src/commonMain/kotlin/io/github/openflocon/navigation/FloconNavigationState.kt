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
        if (route is PanelRoute) {
            val index = _stack.indexOfFirst { it is PanelRoute }

            if (index != -1) {
                _stack[index] = route
            } else {
                _stack.add(route)
            }
        } else {
            _stack.add(route)
        }
    }

    override fun back(count: Int) {
        repeat(count) { _stack.removeLast() }
    }

    fun menu(route: FloconRoute) {
        _stack[0] = route
    }

}
