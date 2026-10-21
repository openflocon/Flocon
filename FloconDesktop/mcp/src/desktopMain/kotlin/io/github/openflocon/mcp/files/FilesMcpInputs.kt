package io.github.openflocon.mcp.files

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_MAX_CHARS = 20_000
internal const val MAX_CHARS = 200_000

@Serializable
internal data class ListFileRootsInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListFilesInput(
    @McpDescription(
        "The id of a root folder returned by list_file_roots, " +
            "or the absolute path of a directory returned by list_files. Default: the first root folder.",
    )
    val path: String = "",
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ReadFileInput(
    @McpDescription("Absolute path of the file, from list_files.")
    val path: String,
    @McpDescription("Text content longer than this is truncated, from 100 to $MAX_CHARS. Default $DEFAULT_MAX_CHARS.")
    val maxChars: Int = DEFAULT_MAX_CHARS,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
