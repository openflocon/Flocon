package io.github.openflocon.mcp.badquality

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_ERROR_CONTENT_TYPE = "application/json"

private const val CONFIG_ID_DESCRIPTION = "Id of the config, from list_bad_network_configs."
private const val NAME_DESCRIPTION = "Name displayed in Flocon."
private const val LATENCY_PROBABILITY_DESCRIPTION =
    "Chance, from 0 to 1, that a request is delayed by a random latency between minLatencyMs and maxLatencyMs. Default 0."
private const val MIN_LATENCY_DESCRIPTION = "Minimum latency added to a request, in milliseconds. Default 0."
private const val MAX_LATENCY_DESCRIPTION = "Maximum latency added to a request, in milliseconds. Default 0."
private const val ERROR_PROBABILITY_DESCRIPTION =
    "Chance, from 0 to 1, that a request fails with one of the errors (requires at least one error). Default 0."
private const val ERRORS_DESCRIPTION =
    "The possible errors: when a request fails, one of them is picked according to its weight."

@Serializable
internal data class BadNetworkErrorInput(
    @McpDescription("Increases the probability of this error vs the others. Default 1.")
    val weight: Float = 1f,
    @McpDescription("HTTP status code of the error response, from 100 to 599. Use it OR exceptionClassPath.")
    val httpCode: Int? = null,
    @McpDescription("Body of the error response. Default empty.")
    val body: String? = null,
    @McpDescription("Media type of the error response body. Default \"$DEFAULT_ERROR_CONTENT_TYPE\".")
    val contentType: String? = null,
    @McpDescription(
        "Instead of a response, the request throws this exception. " +
            "Ex: java.net.SocketTimeoutException, java.net.UnknownHostException, java.net.ConnectException, java.io.IOException.",
    )
    val exceptionClassPath: String? = null,
)

@Serializable
internal data class ListBadNetworkConfigsInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class CreateBadNetworkConfigInput(
    @McpDescription(NAME_DESCRIPTION)
    val name: String,
    @McpDescription(LATENCY_PROBABILITY_DESCRIPTION)
    val latencyTriggerProbability: Double = 0.0,
    @McpDescription(MIN_LATENCY_DESCRIPTION)
    val minLatencyMs: Long = 0,
    @McpDescription(MAX_LATENCY_DESCRIPTION)
    val maxLatencyMs: Long = 0,
    @McpDescription(ERROR_PROBABILITY_DESCRIPTION)
    val errorProbability: Double = 0.0,
    @McpDescription(ERRORS_DESCRIPTION)
    val errors: List<BadNetworkErrorInput> = emptyList(),
    @McpDescription("Also activates it right away (the other configs of the app are deactivated). Default false.")
    val enabled: Boolean = false,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class UpdateBadNetworkConfigInput(
    @McpDescription(CONFIG_ID_DESCRIPTION)
    val configId: String,
    @McpDescription(NAME_DESCRIPTION)
    val name: String? = null,
    @McpDescription(LATENCY_PROBABILITY_DESCRIPTION)
    val latencyTriggerProbability: Double? = null,
    @McpDescription(MIN_LATENCY_DESCRIPTION)
    val minLatencyMs: Long? = null,
    @McpDescription(MAX_LATENCY_DESCRIPTION)
    val maxLatencyMs: Long? = null,
    @McpDescription(ERROR_PROBABILITY_DESCRIPTION)
    val errorProbability: Double? = null,
    @McpDescription("$ERRORS_DESCRIPTION Replaces all the existing errors.")
    val errors: List<BadNetworkErrorInput>? = null,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class SetActiveBadNetworkConfigInput(
    @McpDescription("Id of the config to activate, from list_bad_network_configs. Omit it to deactivate the bad network simulation.")
    val configId: String? = null,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class DeleteBadNetworkConfigInput(
    @McpDescription(CONFIG_ID_DESCRIPTION)
    val configId: String,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
