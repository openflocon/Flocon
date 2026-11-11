package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.analytics.repository.AnalyticsPreferencesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class AnalyticsPreferencesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : AnalyticsPreferencesRepository {

    override fun observeExcludedEventNames(packageName: String): Flow<Set<String>> = localSettingsDataSource.analyticsSettings
        .map { it.excluded(packageName) }
        .distinctUntilChanged()

    override suspend fun excludeEventName(packageName: String, eventName: String) {
        localSettingsDataSource.updateAnalyticsSettings { it.withExcluded(packageName, eventName) }
    }

    override suspend fun restoreEventName(packageName: String, eventName: String) {
        localSettingsDataSource.updateAnalyticsSettings { it.withRestored(packageName, eventName) }
    }

    override suspend fun restoreAllEventNames(packageName: String) {
        localSettingsDataSource.updateAnalyticsSettings { it.withAllRestored(packageName) }
    }
}
