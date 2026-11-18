package io.github.openflocon.flocondesktop.core.data.settings.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AnalyticsSettingsLocal(

    @SerialName("excluded_event_names_by_package")
    val excludedEventNamesByPackage: Map<String, Set<String>> = emptyMap(),
) {

    fun excluded(packageName: String): Set<String> = excludedEventNamesByPackage[packageName].orEmpty()

    fun withExcluded(packageName: String, eventName: String): AnalyticsSettingsLocal = withNames(packageName, excluded(packageName) + eventName)

    fun withRestored(packageName: String, eventName: String): AnalyticsSettingsLocal = withNames(packageName, excluded(packageName) - eventName)

    fun withAllRestored(packageName: String): AnalyticsSettingsLocal = withNames(packageName, emptySet())

    // a package with no names left is dropped, so the stored map only holds what is excluded
    private fun withNames(packageName: String, names: Set<String>): AnalyticsSettingsLocal = copy(
        excludedEventNamesByPackage = if (names.isEmpty()) {
            excludedEventNamesByPackage - packageName
        } else {
            excludedEventNamesByPackage + (packageName to names)
        },
    )
}
