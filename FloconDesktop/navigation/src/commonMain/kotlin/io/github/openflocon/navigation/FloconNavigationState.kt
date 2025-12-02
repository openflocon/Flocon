package io.github.openflocon.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Immutable
interface FloconNavigationState<T : Any> {
    val stack: SnapshotStateList<T>

    fun navigate(route: T)

    // TODO Remove, since it's multiple window handling
    // Maybe not if we instansiate one per window
    fun back(count: Int = 1)

    fun remove(route: FloconRoute)
}

@Immutable
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

    override fun remove(route: FloconRoute) {
        _stack.remove(route)
    }

    fun menu(route: FloconRoute) {
        _stack[0] = route
    }
}
