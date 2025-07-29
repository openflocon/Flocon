package com.florent37.flocondesktop.features.files.domain.model

import kotlin.time.Instant

data class FileDomainModel(
    val name: String,
    val isDirectory: Boolean,
    val path: FilePathDomainModel,
    val size: Long,
    val lastModified: Instant,
)
