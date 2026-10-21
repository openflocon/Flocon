package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.menu.repository.MenuPreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow

internal class MenuPreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : MenuPreferencesRepository {

    override val pinnedItems: Flow<List<String>> = localSettingsDataSource.pinnedMenuItems

    override val collapsedSections: Flow<Set<String>> = localSettingsDataSource.collapsedMenuSections

    override suspend fun setPinnedItems(items: List<String>) {
        localSettingsDataSource.setPinnedMenuItems(items)
    }

    override suspend fun setCollapsedSections(sections: Set<String>) {
        localSettingsDataSource.setCollapsedMenuSections(sections)
    }
}
