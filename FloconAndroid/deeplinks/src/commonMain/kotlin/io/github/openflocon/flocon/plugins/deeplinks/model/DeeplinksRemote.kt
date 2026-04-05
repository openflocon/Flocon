package io.github.openflocon.flocon.plugins.deeplinks.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
internal class DeeplinkRemote(
    val label: String? = null,
    val link: String,
    val description: String? = null,
    val parameters: List<DeeplinkParameterRemote>,
)

@Serializable
internal class DeeplinksRemote(
    val deeplinks: List<DeeplinkRemote>,
    val variables: List<DeeplinkVariableRemote>
)

@Serializable
internal data class DeeplinkVariableRemote(
    val name: String,
    val mode: Mode = Mode.Input,
    val description: String? = null
) {

    @Serializable
    @JsonClassDiscriminator("mode")
    sealed interface Mode {
        @SerialName("input")
        object Input : Mode

        @SerialName("auto_complete")
        data class AutoComplete(val suggestions: List<String>) : Mode
    }

}

@JsonClassDiscriminator("type")
internal sealed interface DeeplinkParameterRemote {
    val name: String

    @Serializable
    @SerialName("auto_complete")
    data class AutoComplete(
        override val name: String,
        val autoComplete: List<String>
    ) : DeeplinkParameterRemote

    @Serializable
    @SerialName("variable")
    data class Variable(
        override val name: String,
        val variableName: String
    ) : DeeplinkParameterRemote
}