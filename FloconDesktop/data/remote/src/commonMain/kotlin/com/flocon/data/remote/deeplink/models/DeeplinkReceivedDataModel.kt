@file:OptIn(ExperimentalSerializationApi::class)

package com.flocon.data.remote.deeplink.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
internal data class DeeplinkReceivedDataModel(
    val label: String? = null,
    val link: String,
    val description: String? = null,
    val parameters: List<Parameter> = emptyList(),
) {

    @Serializable
    @JsonClassDiscriminator("type")
    sealed interface Parameter {
        val name: String

        @Serializable
        @SerialName("auto_complete")
        data class AutoComplete(
            override val name: String,
            val autoComplete: List<String>
        ) : Parameter

        @Serializable
        @SerialName("variable")
        data class Variable(
            override val name: String,
            val variableName: String
        ) : Parameter

    }


}
