package com.flocon.library.domain.models

import kotlin.time.Instant

data class FileDomainModel(
    val name: String,
    val isDirectory: Boolean,
    val path: FilePathDomainModel,
    val size: Long,
    val lastModified: Instant,
)
