package io.github.openflocon.mcp.deeplink

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinksMcpResult(
    val deviceId: String,
    val packageName: String,
    val deeplinks: List<DeeplinkMcpModel>,
    val variables: List<DeeplinkVariableMcpModel>,
)

@Serializable
internal data class DeeplinkHistoryMcpResult(
    val deviceId: String,
    val packageName: String,
    val deeplinks: List<DeeplinkMcpModel>,
)

@Serializable
internal data class DeeplinkMcpModel(
    val id: Long,
    val label: String?,
    val link: String,
    val description: String?,
    val parameters: List<DeeplinkParameterMcpModel>,
)

@Serializable
internal data class DeeplinkParameterMcpModel(
    val name: String,
    val type: DeeplinkParameterType,
    val suggestions: List<String>? = null,
    val variableName: String? = null,
)

@Serializable
internal data class DeeplinkVariableMcpModel(
    val name: String,
    val description: String?,
    val mode: DeeplinkVariableMode,
    val suggestions: List<String>? = null,
)

@Serializable
internal data class OpenDeeplinkMcpResult(
    val deviceId: String,
    val packageName: String,
    val link: String,
    /** Output of the command that opened the deeplink */
    val output: String,
)

@Serializable
internal enum class DeeplinkParameterType {
    @SerialName("auto_complete")
    AutoComplete,

    @SerialName("variable")
    Variable,
}

@Serializable
internal enum class DeeplinkVariableMode {
    @SerialName("input")
    Input,

    @SerialName("auto_complete")
    AutoComplete,
}
