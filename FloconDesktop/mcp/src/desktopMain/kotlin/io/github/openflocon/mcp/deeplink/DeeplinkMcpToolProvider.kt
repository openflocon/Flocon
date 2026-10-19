package io.github.openflocon.mcp.deeplink

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.DeeplinkVariableDomainModel
import io.github.openflocon.domain.deeplink.repository.DeeplinkRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.coroutines.flow.first

internal class DeeplinkMcpToolProvider(
    private val deeplinkRepository: DeeplinkRepository,
    private val deeplinkOpener: DeeplinkOpener,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTypedTool(
            name = "list_deeplinks",
            title = "List deeplinks",
            description = "Lists the deeplinks declared by the mobile app (link, label, description, parameters), " +
                "and the variables their parameters can use.",
            annotations = readOnlyToolAnnotations,
            handler = ::listDeeplinks,
        )

        server.addTypedTool(
            name = "list_deeplink_history",
            title = "List deeplink history",
            description = "Lists the deeplinks recently opened from Flocon for the mobile app, with the parameters used.",
            annotations = readOnlyToolAnnotations,
            handler = ::listHistory,
        )

        server.addTypedTool(
            name = "open_deeplink",
            title = "Open deeplink",
            description = "Opens a deeplink on the device (adb on Android, the booted Simulator on iOS), in the mobile app. " +
                "Unlike the other deeplink tools, this one acts on the device. " +
                "Fill all the parameters of the link first (see list_deeplinks).",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::openDeeplink,
        )
    }

    private suspend fun listDeeplinks(input: ListDeeplinksInput): DeeplinksMcpResult {
        val target = targetResolver.resolve(input)
        val deeplinks = deeplinkRepository.observe(target).first()
        return DeeplinksMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            deeplinks = deeplinks.deeplinks.map { it.toMcp() },
            variables = deeplinks.variables.map { it.toMcp() },
        )
    }

    private suspend fun listHistory(input: ListDeeplinkHistoryInput): DeeplinkHistoryMcpResult {
        val target = targetResolver.resolve(input)
        return DeeplinkHistoryMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            deeplinks = deeplinkRepository.observeHistory(target).first().map { it.toMcp() },
        )
    }

    private suspend fun openDeeplink(input: OpenDeeplinkInput): OpenDeeplinkMcpResult {
        val link = input.link.trim()
        if (link.isEmpty()) throw McpToolException("link must not be empty")
        val target = targetResolver.resolve(input)
        return deeplinkOpener.open(
            link = link,
            deeplinkId = input.deeplinkId ?: -1L,
            saveIntoHistory = input.saveIntoHistory ?: true,
            target = target,
        ).fold(
            doOnFailure = { throw McpToolException("Unable to open the deeplink: ${it.message ?: it::class.simpleName}") },
            doOnSuccess = {
                OpenDeeplinkMcpResult(
                    deviceId = target.deviceId,
                    packageName = target.packageName,
                    link = link,
                    output = it,
                )
            },
        )
    }

    private fun DeeplinkDomainModel.toMcp() = DeeplinkMcpModel(
        id = id,
        label = label,
        link = link,
        description = description,
        parameters = parameters.map { parameter ->
            when (parameter) {
                is DeeplinkDomainModel.Parameter.AutoComplete -> DeeplinkParameterMcpModel(
                    name = parameter.name,
                    type = "auto_complete",
                    suggestions = parameter.autoComplete,
                )
                is DeeplinkDomainModel.Parameter.Variable -> DeeplinkParameterMcpModel(
                    name = parameter.name,
                    type = "variable",
                    variableName = parameter.variableName,
                )
            }
        },
    )

    private fun DeeplinkVariableDomainModel.toMcp() = when (val mode = mode) {
        is DeeplinkVariableDomainModel.Mode.Input -> DeeplinkVariableMcpModel(name = name, description = description, mode = "input")
        is DeeplinkVariableDomainModel.Mode.AutoComplete -> DeeplinkVariableMcpModel(
            name = name,
            description = description,
            mode = "auto_complete",
            suggestions = mode.suggestions,
        )
    }
}
