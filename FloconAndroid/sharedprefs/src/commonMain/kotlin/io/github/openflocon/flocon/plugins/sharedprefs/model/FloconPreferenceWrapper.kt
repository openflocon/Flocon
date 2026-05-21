package io.github.openflocon.flocon.plugins.sharedprefs.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PreferencesDescriptor(
    val name: String,
)