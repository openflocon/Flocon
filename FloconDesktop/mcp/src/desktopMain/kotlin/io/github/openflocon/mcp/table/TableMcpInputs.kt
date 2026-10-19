package io.github.openflocon.mcp.table

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 50
internal const val MAX_LIMIT = 500

@Serializable
internal data class ListTablesInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListTableItemsInput(
    @McpDescription("Id of the table, from list_tables.")
    val tableId: Long,
    @McpDescription("Only the rows containing this text (case insensitive) in one of their values.")
    val text: String? = null,
    @McpDescription("Sort order on the time the rows were received. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of rows returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
