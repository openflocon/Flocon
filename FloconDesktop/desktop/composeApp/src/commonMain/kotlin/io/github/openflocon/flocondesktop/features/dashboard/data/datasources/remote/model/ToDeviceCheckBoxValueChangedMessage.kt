package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceCheckBoxValueChangedMessage(
    val id: String,
    val value: Boolean,
)
