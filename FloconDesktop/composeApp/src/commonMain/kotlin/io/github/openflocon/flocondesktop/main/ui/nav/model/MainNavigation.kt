package io.github.openflocon.flocondesktop.main.ui.nav.model

sealed interface MainNavigation {
    data object Network : MainNavigation

    data object  Dashboard : MainNavigation

    data object Images : MainNavigation

    // storage
    data object Database : MainNavigation
    data object Files : MainNavigation
    data object SharedPreferences : MainNavigation

    data object  Analytics : MainNavigation
    data object  Tables : MainNavigation

    data object  Settings : MainNavigation

    data object  Deeplinks : MainNavigation
}
