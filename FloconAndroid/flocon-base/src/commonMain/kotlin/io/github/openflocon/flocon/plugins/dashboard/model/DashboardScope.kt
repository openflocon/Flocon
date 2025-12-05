package io.github.openflocon.flocon.plugins.dashboard.model

import io.github.openflocon.flocon.plugins.dashboard.builder.FormBuilder
import io.github.openflocon.flocon.plugins.dashboard.builder.SectionBuilder
import kotlinx.coroutines.flow.StateFlow

interface DashboardScope {
    fun <T> section(name: String, flow: StateFlow<T>, content: SectionBuilder.(T) -> Unit)
    fun section(name: String, content: SectionBuilder.() -> Unit)
    
    fun <T> form(
        name: String,
        submitText: String = "Submit",
        onSubmitted: (Map<String, String>) -> Unit,
        flow: StateFlow<T>,
        content: FormBuilder.(T) -> Unit
    )
    
    fun form(
        name: String,
        submitText: String = "Submit",
        onSubmitted: (Map<String, String>) -> Unit,
        content: FormBuilder.() -> Unit
    )
}