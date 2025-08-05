package io.github.openflocon.flocondesktop.main.ui.nav

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.openflocon.flocondesktop.main.ui.nav.model.MainNavigation

@Immutable
interface MainNavigator {
    val backStack: SnapshotStateList<MainNavigation>
    fun setCurrentPage(navigation: MainNavigation)
    fun addNavOnTop(navigation: MainNavigation)
}


