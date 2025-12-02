package com.flocon.data.remote.version.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseRemote(
    @SerialName("tag_name") val tagName: String,
)
