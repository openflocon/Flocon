package io.github.openflocon.mcp.images

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 50
internal const val MAX_LIMIT = 500

@Serializable
internal data class ListImagesInput(
    @McpDescription("Only the images whose url contains this text.")
    val text: String? = null,
    @McpDescription("Only the images loaded after this epoch timestamp, in milliseconds.")
    val sinceTimestamp: Long? = null,
    @McpDescription("Include the response headers of each image. Default false.")
    val includeHeaders: Boolean = false,
    @McpDescription("Sort order on the load time. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of images returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
