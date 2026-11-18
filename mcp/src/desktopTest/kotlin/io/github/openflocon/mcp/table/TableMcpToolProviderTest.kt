package io.github.openflocon.mcp.table

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.domain.table.repository.TableRepository
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val table = TableDomainModel(
        name = "users",
        columns = listOf("id", "name"),
        items = listOf(
            TableDomainModel.TableItem(itemId = "r1", createdAt = 10, values = listOf("1", "Alice")),
            TableDomainModel.TableItem(itemId = "r2", createdAt = 20, values = listOf("2", "Bob")),
            TableDomainModel.TableItem(itemId = "r3", createdAt = 30, values = listOf("3", "alicia")),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            TableMcpToolProvider(
                tableRepository = FakeTableRepository(table),
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking { server.stop() }

    @Test
    fun `all the tools are read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_tables", "list_table_items"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_tables`() = runBlocking {
        val tables = server.callTool("list_tables").json()["tables"]!!.jsonArray

        assertEquals(1, tables.size)
        assertEquals(7, tables.first().jsonObject["tableId"]!!.jsonPrimitive.int)
        assertEquals("users", tables.first().jsonObject["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_table_items returns the rows newest first, by column name`() = runBlocking {
        val result = server.callTool("list_table_items", mapOf("tableId" to 7)).json()

        assertEquals(listOf("r3", "r2", "r1"), result.rowIds())
        assertEquals(listOf("id", "name"), result["columns"]!!.jsonArray.map { it.jsonPrimitive.content })
        val bob = result["rows"]!!.jsonArray[1].jsonObject
        assertEquals("Bob", bob["values"]!!.jsonObject["name"]!!.jsonPrimitive.content)
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `list_table_items filters, orders and limits`() = runBlocking {
        assertEquals(
            listOf("r3", "r1"),
            server.callTool("list_table_items", mapOf("tableId" to 7, "text" to "ALIC")).json().rowIds(),
        )
        val limited = server.callTool("list_table_items", mapOf("tableId" to 7, "order" to "oldest_first", "limit" to 2)).json()
        assertEquals(listOf("r1", "r2"), limited.rowIds())
        assertEquals(true, limited["hasMore"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `unknown table is an error`() = runBlocking {
        val result = server.callTool("list_table_items", mapOf("tableId" to 99))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("99"))
    }

    private fun JsonObject.rowIds(): List<String> = this["rows"]!!.jsonArray.map { it.jsonObject["itemId"]!!.jsonPrimitive.content }
}

private class FakeTableRepository(private val table: TableDomainModel) : TableRepository {
    override fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?> = flowOf(table.takeIf { tableId == 7L })

    override fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>> = flowOf(listOf(TableIdentifierDomainModel(id = 7, name = table.name)))

    override fun observeTableItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, itemId: String): Flow<TableDomainModel?> = error("not used")
    override suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel) = error("not used")
    override suspend fun selectDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId) = error("not used")
    override fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?> = error("not used")
    override suspend fun deleteItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, itemId: String) = error("not used")
    override suspend fun deleteBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, itemId: String) = error("not used")
}
