package io.github.openflocon.navigation

class FloconNavigationState internal constructor() {

    private val _stack = mutableListOf<FloconRoute>(LoadingRoute)
    val stack: List<FloconRoute> = _stack

    fun navigate(route: FloconRoute) {
        _stack.add(route)
    }

    fun back(count: Int = 1) {
        repeat(count) { _stack.removeLast() }
    }

}
