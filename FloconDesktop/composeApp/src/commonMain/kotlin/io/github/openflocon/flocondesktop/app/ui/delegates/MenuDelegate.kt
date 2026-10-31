package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceInstalledPluginsUseCase
import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.ToggleCollapsedMenuSectionUseCase
import io.github.openflocon.domain.menu.usecase.TogglePinnedMenuItemUseCase
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.id
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.applyPreferences
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.buildMenu
import io.github.openflocon.flocondesktop.app.ui.settings.SettingsRoutes
import io.github.openflocon.flocondesktop.features.accessibility.AccessibilityRoutes
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderRoutes
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.crashreporter.CrashReporterRoutes
import io.github.openflocon.flocondesktop.features.dashboard.DashboardRoutes
import io.github.openflocon.flocondesktop.features.database.DatabaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkRoutes
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagsRoutes
import io.github.openflocon.flocondesktop.features.files.FilesRoutes
import io.github.openflocon.flocondesktop.features.images.ImageRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesRoutes
import io.github.openflocon.flocondesktop.features.table.TableRoutes
import io.github.openflocon.flocondesktop.features.timeline.TimelineRoutes
import io.github.openflocon.flocondesktop.features.webview.WebViewRoutes
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * The left menu, shared by the main window and the windows opened for a device: the items depend on the plugins
 * installed in the SDK of the current app, and on the user preferences (pins, collapsed sections).
 * Built from the scope of the window, so it follows its device and its navigation.
 */
internal class MenuDelegate(
    observeCurrentDeviceInstalledPluginsUseCase: ObserveCurrentDeviceInstalledPluginsUseCase,
    observeMenuPreferencesUseCase: ObserveMenuPreferencesUseCase,
    private val togglePinnedMenuItemUseCase: TogglePinnedMenuItemUseCase,
    private val toggleCollapsedMenuSectionUseCase: ToggleCollapsedMenuSectionUseCase,
    private val navigationState: MainFloconNavigationState,
) {

    val menuState: Flow<MenuState> = combine(
        observeCurrentDeviceInstalledPluginsUseCase(),
        observeMenuPreferencesUseCase(),
    ) { installedPlugins, preferences ->
        buildMenu().applyPreferences(
            installedPlugins = installedPlugins,
            pinnedIds = preferences.pinnedItems,
            collapsedSectionIds = preferences.collapsedSections,
            hiddenIds = preferences.hiddenItems,
        )
    }

    fun initialMenuState(): MenuState = buildMenu().applyPreferences(
        installedPlugins = null,
        pinnedIds = emptyList(),
        collapsedSectionIds = emptySet(),
    )

    /** The settings are opened over the current screen, the others replace it. */
    fun navigateTo(screen: SubScreen) {
        val route = screen.toRoute()
        if (route !is SettingsRoutes) {
            navigationState.menu(route)
        } else {
            navigationState.navigate(route)
        }
    }

    suspend fun togglePin(screen: SubScreen) = togglePinnedMenuItemUseCase(screen.id)

    suspend fun toggleSection(sectionId: String) = toggleCollapsedMenuSectionUseCase(sectionId)
}

private fun SubScreen.toRoute(): FloconRoute = when (this) {
    SubScreen.Analytics -> AnalyticsRoutes.Main
    SubScreen.FeatureFlags -> FeatureFlagsRoutes.Main
    SubScreen.Accessibility -> AccessibilityRoutes.Main
    SubScreen.Dashboard -> DashboardRoutes.Main
    SubScreen.Database -> DatabaseRoutes.Main
    SubScreen.Deeplinks -> DeeplinkRoutes.Main
    SubScreen.AdbCommander -> AdbCommanderRoutes.Main
    SubScreen.Files -> FilesRoutes.Main
    SubScreen.Images -> ImageRoutes.Main
    SubScreen.Network -> NetworkRoutes.Main
    SubScreen.Settings -> SettingsRoutes.Main
    SubScreen.SharedPreferences -> SharedPreferencesRoutes.Main
    SubScreen.Tables -> TableRoutes.Main
    SubScreen.CrashReporter -> CrashReporterRoutes.Main
    SubScreen.Timeline -> TimelineRoutes.Main
    SubScreen.WebView -> WebViewRoutes.Main
}
