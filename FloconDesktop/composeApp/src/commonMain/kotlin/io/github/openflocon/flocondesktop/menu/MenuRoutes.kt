package io.github.openflocon.flocondesktop.menu

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.menu.ui.MenuScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import kotlinx.serialization.Serializable
import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

internal sealed interface MainRoutes : FloconRoute {

    @Serializable
    data object Main : MainRoutes

    // For later
    @Serializable
    data class Sub(
        val id: String
    ) : MainRoutes, KoinScopeComponent {
        override val scope: Scope
            get() = createScope(scopeId = id)

        companion object {
            val Main = Sub(id = "main")
        }
    }

}

internal fun Koin.createFloconScope(sub: MainRoutes.Sub) = createScope(sub)

fun EntryProviderScope<FloconRoute>.menuRoutes() {
    entry<MainRoutes.Main> { MenuScreen() }
    // TODO Scope VM on this
    entry<MainRoutes.Sub>(metadata = WindowSceneStrategy.window()) {
        MenuScreen() // TODO Remove toolbar
    }
}
