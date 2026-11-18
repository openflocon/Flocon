package io.github.openflocon.mcp.files

import kotlinx.serialization.Serializable

@Serializable
internal data class FilesMcpResult(
    val deviceId: String,
    val packageName: String,
    val path: String,
    val fromCache: Boolean,
    val warning: String? = null,
    val files: List<FileMcpModel>,
)

@Serializable
internal data class FileRootsMcpResult(
    val deviceId: String,
    val packageName: String,
    val fromCache: Boolean,
    val warning: String? = null,
    val roots: List<FileRootMcpModel>,
)

@Serializable
internal data class FileRootMcpModel(
    val id: String,
    val name: String,
    val path: String,
)

@Serializable
internal data class FileMcpModel(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: String,
)

@Serializable
internal data class FileContentMcpModel(
    val path: String,
    val size: Long,
    val type: String,
    val mimeType: String?,
    val content: String?,
    val truncated: Boolean,
    val localPath: String,
    val hint: String?,
)
