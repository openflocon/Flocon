package io.github.openflocon.data.local.accessibility

import io.github.openflocon.data.core.accessibility.datasource.AccessibilityFocusedNodeDataSource
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityLocalDataSource
import io.github.openflocon.data.core.accessibility.datasource.AccessibilityStateDataSource
import io.github.openflocon.data.local.accessibility.datasource.AccessibilityFocusedNodeDataSourceInMemory
import io.github.openflocon.data.local.accessibility.datasource.AccessibilityLocalDataSourceRoom
import io.github.openflocon.data.local.accessibility.datasource.AccessibilityStateDataSourceInMemory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val accessibilityLocalModule = module {
    singleOf(::AccessibilityLocalDataSourceRoom) bind AccessibilityLocalDataSource::class
    singleOf(::AccessibilityStateDataSourceInMemory) bind AccessibilityStateDataSource::class
    singleOf(::AccessibilityFocusedNodeDataSourceInMemory) bind AccessibilityFocusedNodeDataSource::class
}
