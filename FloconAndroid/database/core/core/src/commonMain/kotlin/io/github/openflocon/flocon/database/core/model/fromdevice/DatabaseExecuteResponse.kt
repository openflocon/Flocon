@file:OptIn(ExperimentalSerializationApi::class)

package io.github.openflocon.flocon.database.core.model.fromdevice

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed interface DatabaseExecuteResponse