@file:OptIn(ExperimentalSerializationApi::class)

package io.github.openflocon.flocon.plugins.deeplinks.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
internal sealed interface DeeplinkParameterRemote {
    val name: String

    @Serializable
    @SerialName("auto_complete")
    class AutoComplete(
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

@Serializable
internal class DeeplinkRemote(
    val label: String? = null,
    val link: String,
    val description: String? = null,
    val parameters: List<DeeplinkParameterRemote>
)

@Serializable
internal data class DeeplinkVariableRemote(
    val name: String,
    val description: String? = null
)

@Serializable
internal class DeeplinksRemote(
    val deeplinks: List<DeeplinkRemote>,
    val variables: List<DeeplinkVariableRemote>
)
