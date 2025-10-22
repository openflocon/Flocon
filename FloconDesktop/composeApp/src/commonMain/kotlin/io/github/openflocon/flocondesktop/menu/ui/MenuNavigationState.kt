package io.github.openflocon.flocondesktop.menu.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.navigation.FloconNavigationState

class MenuNavigationState(initialScreen: SubScreen) : FloconNavigationState<SubScreen> {
    private val _stack = mutableStateListOf(initialScreen)
    override val stack: SnapshotStateList<SubScreen> = _stack

    override fun navigate(route: SubScreen) {
        _stack.add(route)
    }

    override fun back(count: Int) {
        repeat(count) { _stack.removeLast() }
    }
}
