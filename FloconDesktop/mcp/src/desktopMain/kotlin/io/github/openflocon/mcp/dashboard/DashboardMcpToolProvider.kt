package io.github.openflocon.mcp.dashboard

import io.github.openflocon.domain.dashboard.models.DashboardContainerDomainModel
import io.github.openflocon.domain.dashboard.models.DashboardElementDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import kotlinx.coroutines.flow.first

internal class DashboardMcpToolProvider(
    private val dashboardRepository: DashboardRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_dashboards",
            title = "List dashboards",
            description = "Lists the ids of the dashboards sent by the mobile app (custom screens made of texts, buttons, forms...), " +
                "and the one selected in Flocon Desktop.",
            annotations = readOnlyToolAnnotations,
            handler = ::listDashboards,
        )

        server.addTypedTool(
            name = "get_dashboard",
            title = "Get a dashboard",
            description = "Returns a dashboard of the mobile app with its containers and elements (texts, values, check boxes, " +
                "text fields, buttons...), as the app last sent it. Read only: buttons and fields cannot be triggered.",
            annotations = readOnlyToolAnnotations,
            handler = ::getDashboard,
        )
    }

    private suspend fun listDashboards(input: ListDashboardsInput): DashboardsMcpResult {
        val target = targetResolver.resolve(input)
        return DashboardsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            selectedDashboardId = dashboardRepository.observeSelectedDeviceDashboard(target).first(),
            dashboardIds = dashboardRepository.observeDeviceDashboards(target).first(),
        )
    }

    private suspend fun getDashboard(input: GetDashboardInput): DashboardMcpResult {
        val target = targetResolver.resolve(input)
        val dashboardId = input.dashboardId?.takeIf { it.isNotBlank() }
            ?: dashboardRepository.observeSelectedDeviceDashboard(target).first()
            ?: throw McpToolException("No dashboard is selected, pass a dashboardId (see list_dashboards)")

        val dashboard = dashboardRepository.observeDashboard(target, dashboardId).first()
            ?: throw McpToolException("Dashboard \"$dashboardId\" not found, see list_dashboards")

        return DashboardMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            dashboardId = dashboard.dashboardId,
            containers = dashboard.containers.map { it.toMcp() },
        )
    }

    private fun DashboardContainerDomainModel.toMcp(): DashboardContainerMcpModel {
        val form = containerConfig as? FormContainerConfigDomainModel
        return DashboardContainerMcpModel(
            name = name,
            type = when (containerConfig) {
                is FormContainerConfigDomainModel -> "form"
                SectionContainerConfigDomainModel -> "section"
            },
            formId = form?.formId,
            submitText = form?.submitText,
            elements = elements.map { it.toMcp() },
        )
    }

    private fun DashboardElementDomainModel.toMcp(): DashboardElementMcpModel = when (this) {
        is DashboardElementDomainModel.Button -> DashboardElementMcpModel(type = "button", label = text, id = id)

        is DashboardElementDomainModel.Text -> DashboardElementMcpModel(type = "text", label = label, value = value, color = color)

        is DashboardElementDomainModel.Label -> DashboardElementMcpModel(type = "label", label = label, color = color)

        is DashboardElementDomainModel.PlainText -> DashboardElementMcpModel(
            type = when (type) {
                DashboardElementDomainModel.PlainText.Type.Text -> "plain_text"
                DashboardElementDomainModel.PlainText.Type.Json -> "json"
            },
            label = label,
            value = value,
        )

        is DashboardElementDomainModel.TextField -> DashboardElementMcpModel(
            type = "text_field",
            label = label,
            value = value,
            placeHolder = placeHolder,
            id = id,
        )

        is DashboardElementDomainModel.CheckBox -> DashboardElementMcpModel(type = "check_box", label = label, checked = value, id = id)

        is DashboardElementDomainModel.Markdown -> DashboardElementMcpModel(type = "markdown", label = label, value = value)

        is DashboardElementDomainModel.Html -> DashboardElementMcpModel(type = "html", label = label, value = value)
    }
}
