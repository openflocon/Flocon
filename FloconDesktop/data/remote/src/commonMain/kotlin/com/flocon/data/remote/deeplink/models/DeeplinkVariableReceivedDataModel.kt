package com.flocon.data.remote.deeplink.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
internal data class DeeplinkVariableReceivedDataModel(
    val name: String,
    val mode: Mode = Mode.Input,
    val description: String? = null
) {

    @Serializable
    @JsonClassDiscriminator("type")
    sealed interface Mode {

        @Serializable
        @SerialName("input")
        object Input : Mode

        @Serializable
        @SerialName("auto_complete")
        data class AutoComplete(val suggestions: List<String>) : Mode

    }

}
