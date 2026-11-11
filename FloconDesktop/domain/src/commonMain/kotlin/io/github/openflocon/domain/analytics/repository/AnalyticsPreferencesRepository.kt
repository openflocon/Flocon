package io.github.openflocon.domain.analytics.repository

import kotlinx.coroutines.flow.Flow

interface AnalyticsPreferencesRepository {
    fun observeExcludedEventNames(packageName: String): Flow<Set<String>>

    suspend fun excludeEventName(packageName: String, eventName: String)

    suspend fun restoreEventName(packageName: String, eventName: String)

    suspend fun restoreAllEventNames(packageName: String)
}
