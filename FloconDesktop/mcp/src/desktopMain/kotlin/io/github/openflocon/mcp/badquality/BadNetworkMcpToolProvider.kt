package io.github.openflocon.mcp.badquality

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.isSameApp
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import io.github.openflocon.mcp.McpToolServer
import kotlinx.coroutines.flow.first
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * "Bad network" simulation: when a config is active, the app delays and fails a part of its requests.
 * Only one config is active at a time, per app.
 */
internal class BadNetworkMcpToolProvider(
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val devicesRepository: DevicesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_bad_network_configs",
            title = "List bad network configs",
            description = "Lists the bad network configs of the mobile app, and which one is active. " +
                "A config simulates a bad network: a part of the requests are delayed, a part fail with an error response or an exception.",
            annotations = readOnlyToolAnnotations,
            handler = ::listConfigs,
        )

        server.addTypedTool(
            name = "create_bad_network_config",
            title = "Create bad network config",
            description = "Creates a bad network config. Ex: slow network = latencyTriggerProbability 1, minLatencyMs 2000, maxLatencyMs 5000; " +
                "flaky network = errorProbability 0.3 with a java.net.SocketTimeoutException error and a 503 error. " +
                "It is not active until enabled = true, or set_active_bad_network_config.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::createConfig,
        )

        server.addTypedTool(
            name = "update_bad_network_config",
            title = "Update bad network config",
            description = "Updates a bad network config: only the given arguments are changed. " +
                "To activate / deactivate it, use set_active_bad_network_config.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::updateConfig,
        )

        server.addTypedTool(
            name = "set_active_bad_network_config",
            title = "Activate bad network config",
            description = "Activates a bad network config: the other configs of the app are deactivated, and the app starts to " +
                "receive delays / errors immediately. Without configId, the bad network simulation is turned off.",
            annotations = writeToolAnnotations(destructive = false, idempotent = true),
            handler = ::setActiveConfig,
        )

        server.addTypedTool(
            name = "delete_bad_network_config",
            title = "Delete bad network config",
            description = "Deletes a bad network config. If it was active, the bad network simulation is turned off.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::deleteConfig,
        )
    }

    private suspend fun listConfigs(input: ListBadNetworkConfigsInput): BadNetworkConfigsMcpResult {
        val target = targetResolver.resolve(input)
        return BadNetworkConfigsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            configs = networkBadQualityRepository.observeAllNetworkQualities(target).first().map { it.toMcpModel() },
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun createConfig(input: CreateBadNetworkConfigInput): BadNetworkChangeMcpResult {
        val target = targetResolver.resolve(input)
        val config = BadQualityConfigDomainModel(
            id = Uuid.random().toString(),
            isEnabled = false,
            name = input.name.trim().ifEmpty { throw McpToolException("'name' must not be empty") },
            createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            latency = latencyOf(
                triggerProbability = input.latencyTriggerProbability,
                minLatencyMs = input.minLatencyMs,
                maxLatencyMs = input.maxLatencyMs,
            ),
            errorProbability = validProbability("errorProbability", input.errorProbability),
            errors = input.errors.map { it.toDomain() },
        ).also { it.validateErrors() }

        networkBadQualityRepository.saveBadNetworkQuality(target, config)
        if (input.enabled) {
            networkBadQualityRepository.setEnabledConfig(target, config.id)
        }
        val isApplied = sendToApp(target)

        return BadNetworkChangeMcpResult(
            message = when {
                !input.enabled -> "Config created, not active: activate it with set_active_bad_network_config."
                isApplied -> "Config created and active, the app is affected from now on."
                else -> "Config created and active, it will be applied when the app connects to Flocon."
            },
            config = requireConfig(target, config.id).toMcpModel(),
        )
    }

    private suspend fun updateConfig(input: UpdateBadNetworkConfigInput): BadNetworkChangeMcpResult {
        val target = targetResolver.resolve(input)
        val existing = requireConfig(target, input.configId)

        val updated = existing.copy(
            name = input.name?.trim()?.ifEmpty { throw McpToolException("'name' must not be empty") } ?: existing.name,
            latency = latencyOf(
                triggerProbability = input.latencyTriggerProbability ?: existing.latency.triggerProbability,
                minLatencyMs = input.minLatencyMs ?: existing.latency.minLatencyMs,
                maxLatencyMs = input.maxLatencyMs ?: existing.latency.maxLatencyMs,
            ),
            errorProbability = input.errorProbability?.let { validProbability("errorProbability", it) } ?: existing.errorProbability,
            errors = input.errors?.map { it.toDomain() } ?: existing.errors,
        ).also { it.validateErrors() }

        networkBadQualityRepository.saveBadNetworkQuality(target, updated)
        // an active config is sent again to the app
        sendToApp(target)

        return BadNetworkChangeMcpResult(message = "Config updated.", config = updated.toMcpModel())
    }

    private suspend fun setActiveConfig(input: SetActiveBadNetworkConfigInput): BadNetworkChangeMcpResult {
        val target = targetResolver.resolve(input)
        val configId = input.configId?.takeIf { it.isNotBlank() }
        val config = configId?.let { requireConfig(target, it) }

        networkBadQualityRepository.setEnabledConfig(target, configId)
        val isApplied = sendToApp(target)

        return BadNetworkChangeMcpResult(
            message = when {
                config == null -> "Bad network simulation turned off."
                isApplied -> "Config '${config.name}' is active, the app is affected from now on."
                else -> "Config '${config.name}' is active, it will be applied when the app connects to Flocon."
            },
            config = config?.let { requireConfig(target, it.id).toMcpModel() },
        )
    }

    private suspend fun deleteConfig(input: DeleteBadNetworkConfigInput): BadNetworkChangeMcpResult {
        val target = targetResolver.resolve(input)
        val config = requireConfig(target, input.configId)

        networkBadQualityRepository.deleteNetworkQuality(target, config.id)
        sendToApp(target)

        return BadNetworkChangeMcpResult(message = "Config '${config.name}' deleted.", config = null)
    }

    /**
     * The app only keeps the active config: send it (or nothing) to the app, if connected
     *
     * @return true if [target] is connected
     */
    private suspend fun sendToApp(target: DeviceIdAndPackageNameDomainModel): Boolean {
        val isConnected = devicesRepository.activeDevices.first().any { it.isSameApp(target) }
        if (isConnected) {
            networkBadQualityRepository.setupBadNetworkQuality(
                deviceIdAndPackageName = target,
                config = networkBadQualityRepository.getTheOnlyEnabledNetworkQuality(target),
            )
        }
        return isConnected
    }

    private suspend fun requireConfig(
        target: DeviceIdAndPackageNameDomainModel,
        configId: String,
    ): BadQualityConfigDomainModel = networkBadQualityRepository.getNetworkQuality(target, configId)
        ?: throw McpToolException("Bad network config '$configId' not found. Use list_bad_network_configs to get valid ids.")

    private fun latencyOf(
        triggerProbability: Double,
        minLatencyMs: Long,
        maxLatencyMs: Long,
    ): BadQualityConfigDomainModel.LatencyConfig {
        if (minLatencyMs < 0) throw McpToolException("'minLatencyMs' must be positive")
        if (maxLatencyMs < minLatencyMs) throw McpToolException("'maxLatencyMs' must be greater than or equal to 'minLatencyMs'")
        return BadQualityConfigDomainModel.LatencyConfig(
            triggerProbability = validProbability("latencyTriggerProbability", triggerProbability),
            minLatencyMs = minLatencyMs,
            maxLatencyMs = maxLatencyMs,
        )
    }

    private fun validProbability(argument: String, value: Double): Double {
        if (value.isNaN() || value !in 0.0..1.0) throw McpToolException("'$argument' must be between 0 and 1")
        return value
    }

    private fun BadQualityConfigDomainModel.validateErrors() {
        if (errorProbability > 0 && errors.isEmpty()) {
            throw McpToolException("'errorProbability' is greater than 0: give at least one error in 'errors'")
        }
    }

    private fun BadNetworkErrorInput.toDomain(): BadQualityConfigDomainModel.Error {
        if (weight.isNaN() || weight <= 0f) throw McpToolException("The 'weight' of an error must be greater than 0")
        val exceptionClassPath = exceptionClassPath?.takeIf { it.isNotBlank() }
        val hasBodyArguments = httpCode != null || body != null || contentType != null

        return BadQualityConfigDomainModel.Error(
            weight = weight,
            type = when {
                exceptionClassPath != null && hasBodyArguments -> throw McpToolException(
                    "An error either throws an exception or returns a response: 'exceptionClassPath' can't be used " +
                        "with 'httpCode', 'body' or 'contentType'",
                )

                exceptionClassPath != null -> BadQualityConfigDomainModel.Error.Type.Exception(classPath = exceptionClassPath)

                httpCode == null -> throw McpToolException("An error needs an 'httpCode' or an 'exceptionClassPath'")

                httpCode !in 100..599 -> throw McpToolException("'httpCode' must be between 100 and 599")

                else -> BadQualityConfigDomainModel.Error.Type.Body(
                    httpCode = httpCode,
                    body = body.orEmpty(),
                    contentType = contentType?.takeIf { it.isNotBlank() } ?: DEFAULT_ERROR_CONTENT_TYPE,
                )
            },
        )
    }
}
