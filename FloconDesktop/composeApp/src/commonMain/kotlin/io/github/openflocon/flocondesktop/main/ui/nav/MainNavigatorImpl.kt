package io.github.openflocon.flocondesktop.main.ui.nav

import androidx.compose.runtime.mutableStateListOf
import io.github.openflocon.flocondesktop.main.ui.nav.model.MainNavigation

class MainNavigatorImpl : MainNavigator {
    private val _backStack = mutableStateListOf<MainNavigation>(MainNavigation.Network)
    override val backStack = _backStack

    override fun setCurrentPage(navigation: MainNavigation) {
        _backStack[0] = navigation
    }

    override fun addNavOnTop(navigation: MainNavigation) {
        _backStack.add(navigation)
    }
}
