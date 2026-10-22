package io.github.openflocon.mcp.table

import io.github.openflocon.domain.table.repository.TableRepository
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

internal class TableMcpToolProvider(
    private val tableRepository: TableRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_tables",
            title = "List tables",
            description = "Lists the custom tables sent by the mobile app (Table plugin), with their tableId.",
            annotations = readOnlyToolAnnotations,
            handler = ::listTables,
        )

        server.addTypedTool(
            name = "list_table_items",
            title = "List table items",
            description = "Lists the rows of a custom table sent by the mobile app, newest first, each row being a column name -> value map. " +
                "Can be filtered with a text searched in the values.",
            annotations = readOnlyToolAnnotations,
            handler = ::listItems,
        )
    }

    private suspend fun listTables(input: ListTablesInput): TablesMcpResult {
        val target = targetResolver.resolve(input)
        return TablesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            tables = tableRepository.observeDeviceTables(target).first().map { TableMcpModel(tableId = it.id, name = it.name) },
        )
    }

    private suspend fun listItems(input: ListTableItemsInput): TableItemsMcpResult {
        val target = targetResolver.resolve(input)
        val limit = input.limit.coerceIn(1, MAX_LIMIT)
        val table = tableRepository.observeTable(target, input.tableId).first()
            ?: throw McpToolException("Table '${input.tableId}' not found, use list_tables to get the ids of the tables.")

        val text = input.text?.takeIf { it.isNotBlank() }
        val matching = table.items
            .filter { item -> text == null || item.values.any { it.contains(text, ignoreCase = true) } }
            .sortedBy { it.createdAt }
            .let { if (input.order == McpOrder.NewestFirst) it.reversed() else it }

        return TableItemsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            tableId = input.tableId,
            name = table.name,
            columns = table.columns,
            returned = matching.take(limit).size,
            hasMore = matching.size > limit,
            rows = matching.take(limit).map { item ->
                TableRowMcpModel(
                    itemId = item.itemId,
                    timestamp = Instant.fromEpochMilliseconds(item.createdAt).toString(),
                    timestampMs = item.createdAt,
                    values = table.columns.zip(item.values).toMap(),
                )
            },
        )
    }
}
