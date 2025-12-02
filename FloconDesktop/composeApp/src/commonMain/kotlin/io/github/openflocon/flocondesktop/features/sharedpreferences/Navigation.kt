package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.SharedPreferencesScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface SharedPreferencesRoutes : FloconRoute {

    @Serializable
    data object Main : SharedPreferencesRoutes
}

fun EntryProviderScope<FloconRoute>.sharedPreferencesRoutes() {
    entry<SharedPreferencesRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        SharedPreferencesScreen()
    }
}
