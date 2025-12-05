package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.dashboard.builder.FormBuilder
import io.github.openflocon.flocon.plugins.dashboard.builder.SectionBuilder
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardConfig
import io.github.openflocon.flocon.plugins.dashboard.model.DashboardScope
import io.github.openflocon.flocon.plugins.dashboard.model.config.ContainerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Creates a reactive dashboard attached to the [CoroutineScope].
 *
 * @param id The unique identifier for the dashboard.
 * @param block The builder block to define sections.
 * @return A [Job] representing the dashboard lifecycle.
 */
fun CoroutineScope.floconDashboard(id: String, block: DashboardScope.() -> Unit): Job {
    // distinct job to allow manual cancellation without affecting the parent scope,
    // and SupervisorJob to prevent child failures from cancelling the parent.
    val job = SupervisorJob(coroutineContext[Job])
    val scope = CoroutineScope(coroutineContext + job)

    val dashboardScope = DashboardScopeImpl()
    dashboardScope.block()

    scope.launch {
        val flows = dashboardScope.flows
        val sectionsFlow = if (flows.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(flows) { it.toList() }
        }

        sectionsFlow.collect { containers ->
            val config = DashboardConfig(id = id, containers = containers)
            FloconApp.instance?.client?.dashboardPlugin?.registerDashboard(config)
        }
    }

    return job
}

internal class DashboardScopeImpl : DashboardScope {
    internal val flows = mutableListOf<Flow<ContainerConfig>>()

    /**
     * Adds a reactive section to the dashboard.
     * The section will be re-rendered whenever the [flow] emits a new value.
     *
     * @param name The name of the section.
     * @param flow The [StateFlow] providing the data for the section.
     * @param content The builder to construct the section content based on the data.
     */
    override fun <T> section(name: String, flow: StateFlow<T>, content: SectionBuilder.(T) -> Unit) {
        val sectionFlow = flow.map { item ->
            SectionBuilder(name)
                .apply { content(item) }
                .build()
        }
        flows.add(sectionFlow)
    }

    /**
     * Adds a static section to the dashboard.
     * The section is rendered immediately and does not update automatically.
     *
     * @param name The name of the section.
     * @param content The builder to construct the section content.
     */
    override fun section(name: String, content: SectionBuilder.() -> Unit) {
        val config = SectionBuilder(name)
            .apply { content() }
            .build()
        flows.add(flowOf(config))
    }

    /**
     * Adds a reactive form to the dashboard.
     * The form will be re-rendered whenever the [flow] emits a new value.
     *
     * @param name The name of the form.
     * @param submitText The text to display on the submit button.
     * @param onSubmitted The callback to be invoked when the form is submitted.
     * @param flow The [StateFlow] providing the data for the form.
     * @param content The builder to construct the form content based on the data.
     */
    override fun <T> form(
        name: String,
        submitText: String,
        onSubmitted: (Map<String, String>) -> Unit,
        flow: StateFlow<T>,
        content: FormBuilder.(T) -> Unit
    ) {
        val formFlow = flow.map { item ->
            FormBuilder(
                name = name,
                submitText = submitText,
                onSubmitted = onSubmitted
            ).apply {
                content(item)
            }.build()
        }
        flows.add(formFlow)
    }

    /**
     * Adds a static form to the dashboard.
     * The form is rendered immediately and does not update automatically.
     *
     * @param name The name of the form.
     * @param submitText The text to display on the submit button.
     * @param onSubmitted The callback to be invoked when the form is submitted.
     * @param content The builder to construct the form content.
     */
    override fun form(
        name: String,
        submitText: String,
        onSubmitted: (Map<String, String>) -> Unit,
        content: FormBuilder.() -> Unit
    ) {
        val config =
            FormBuilder(
                name = name,
                submitText = submitText,
                onSubmitted = onSubmitted
            ).apply { content() }
            .build()
        flows.add(flowOf(config))
    }
}