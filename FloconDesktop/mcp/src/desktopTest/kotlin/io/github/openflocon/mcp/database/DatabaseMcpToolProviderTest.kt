package io.github.openflocon.mcp.database

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
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

class DatabaseMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val databaseRepository = FakeDatabaseRepository(
        database = DeviceDataBaseDomainModel(id = "/data/databases/app.db", name = "app.db"),
        responses = mapOf(
            "SELECT name FROM sqlite_master" to select(listOf("name"), listOf("dogs"), listOf("users")),
            "PRAGMA table_info(\"users\")" to select(
                listOf("cid", "name", "type", "notnull", "dflt_value", "pk"),
                listOf("0", "id", "INTEGER", "1", null, "1"),
                listOf("1", "name", "TEXT", "0", null, "0"),
            ),
            "PRAGMA table_info(\"dogs\")" to select(
                listOf("cid", "name", "type", "notnull", "dflt_value", "pk"),
                listOf("0", "id", "INTEGER", "1", null, "1"),
            ),
            "SELECT * FROM users" to select(
                listOf("id", "name"),
                listOf("1", "x".repeat(50)),
                listOf("2", null),
                listOf("3", "flo"),
            ),
            "SELECT * FROM unknown" to DatabaseExecuteSqlResponseDomainModel.Error(message = "no such table: unknown", originalSql = ""),
        ),
        logs = listOf(
            log("SELECT * FROM users WHERE id = ?", listOf("1"), timestamp = 10, appInstance = 1),
            log("BEGIN TRANSACTION", null, timestamp = 20, appInstance = 2, isTransaction = true),
            log("INSERT INTO users VALUES (?, ?)", listOf("2", "flo"), timestamp = 30, appInstance = 2),
            log("SELECT * FROM dogs", null, timestamp = 40, appInstance = 2),
        ),
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            DatabaseMcpToolProvider(
                databaseRepository = databaseRepository,
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

        assertEquals(
            setOf("list_databases", "list_database_tables", "query_database", "list_database_query_logs"),
            tools.map { it.name }.toSet(),
        )
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_databases asks the app`() = runBlocking {
        val result = server.callTool("list_databases").json()

        assertTrue(databaseRepository.askedForDatabases)
        val database = result["databases"]!!.jsonArray.single().jsonObject
        assertEquals("/data/databases/app.db", database["databaseId"]!!.jsonPrimitive.content)
        assertEquals("app.db", database["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_database_tables returns the schema`() = runBlocking {
        val result = server.callTool("list_database_tables", mapOf("databaseId" to "/data/databases/app.db")).json()

        val tables = result["tables"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("dogs", "users"), tables.map { it["name"]!!.jsonPrimitive.content })
        val usersColumns = tables[1]["columns"]!!.jsonArray.map { it.jsonObject }
        assertEquals(listOf("id", "name"), usersColumns.map { it["name"]!!.jsonPrimitive.content })
        assertEquals(true, usersColumns[0]["primaryKey"]!!.jsonPrimitive.boolean)
        assertEquals(false, usersColumns[0]["nullable"]!!.jsonPrimitive.boolean)
        assertEquals(true, usersColumns[1]["nullable"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `query_database returns the rows`() = runBlocking {
        val result = server.callTool(
            "query_database",
            mapOf("databaseId" to "/data/databases/app.db", "sql" to "SELECT * FROM users", "maxRows" to 2, "maxCellChars" to 10),
        ).json()

        assertEquals(listOf("id", "name"), result["columns"]!!.jsonArray.map { it.jsonPrimitive.content })
        assertEquals(3, result["totalRows"]!!.jsonPrimitive.int)
        val rows = result["rows"]!!.jsonArray.map { it.jsonArray }
        assertEquals(2, rows.size)
        assertEquals("xxxxxxxxxx… [truncated, 50 chars]", rows[0][1].jsonPrimitive.content)
    }

    @Test
    fun `query_database refuses the modifying queries without sending them`() = runBlocking {
        val result = server.callTool("query_database", mapOf("databaseId" to "/data/databases/app.db", "sql" to "DELETE FROM users"))

        assertEquals(true, result.isError)
        assertTrue(databaseRepository.executedQueries.isEmpty())
    }

    @Test
    fun `query_database requires the sql`() = runBlocking {
        val result = server.callTool("query_database", mapOf("databaseId" to "/data/databases/app.db"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("sql"))
        assertTrue(databaseRepository.executedQueries.isEmpty())
    }

    @Test
    fun `query_database returns the SQL errors`() = runBlocking {
        val result = server.callTool("query_database", mapOf("databaseId" to "/data/databases/app.db", "sql" to "SELECT * FROM unknown"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("no such table: unknown"))
    }

    @Test
    fun `query_database when the app does not answer`() = runBlocking {
        val result = server.callTool("query_database", mapOf("databaseId" to "/data/databases/app.db", "sql" to "SELECT * FROM timeout"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("did not answer"))
    }

    @Test
    fun `list_database_query_logs returns the current session queries, newest first`() = runBlocking {
        val result = server.callTool("list_database_query_logs", mapOf("databaseId" to "/data/databases/app.db")).json()

        assertEquals("app.db", result["databaseName"]!!.jsonPrimitive.content)
        assertEquals(false, result["hasMore"]!!.jsonPrimitive.boolean)
        assertEquals(
            listOf("SELECT * FROM dogs", "INSERT INTO users VALUES ('2', 'flo')"),
            result["queries"]!!.jsonArray.map { it.jsonObject["sql"]!!.jsonPrimitive.content },
        )
    }

    @Test
    fun `list_database_query_logs filters`() = runBlocking {
        val result = server.callTool(
            "list_database_query_logs",
            mapOf("databaseId" to "/data/databases/app.db", "text" to "users", "includeOldSessions" to true, "includeTransactions" to true, "limit" to 1),
        ).json()

        assertEquals(true, result["hasMore"]!!.jsonPrimitive.boolean)

        assertEquals(
            listOf("INSERT INTO users VALUES ('2', 'flo')"),
            result["queries"]!!.jsonArray.map { it.jsonObject["sql"]!!.jsonPrimitive.content },
        )
    }

    private fun select(columns: List<String>, vararg rows: List<String?>) = DatabaseExecuteSqlResponseDomainModel.Select(
        columns = columns,
        values = rows.toList(),
    )

    private fun log(
        sql: String,
        args: List<String>?,
        timestamp: Long,
        appInstance: Long,
        isTransaction: Boolean = false,
    ) = DatabaseQueryLogDomainModel(
        dbName = "app.db",
        sqlQuery = sql,
        bindArgs = args,
        timestamp = timestamp,
        isTransaction = isTransaction,
        appInstance = appInstance,
    )
}

/**
 * Answers the queries starting with a key of [responses], the other ones time out
 */
private class FakeDatabaseRepository(
    private val database: DeviceDataBaseDomainModel,
    private val responses: Map<String, DatabaseExecuteSqlResponseDomainModel>,
    private val logs: List<DatabaseQueryLogDomainModel>,
) : DatabaseRepository {

    private val databases = MutableStateFlow<List<DeviceDataBaseDomainModel>>(emptyList())
    var askedForDatabases = false
    val executedQueries = mutableListOf<String>()

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>> = databases

    override suspend fun getDatabaseById(databaseId: String): DeviceDataBaseDomainModel? = database.takeIf { it.id == databaseId }

    override suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        askedForDatabases = true
        databases.value = listOf(database)
    }

    override suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> {
        synchronized(executedQueries) { executedQueries += query }
        return responses.entries.firstOrNull { query.startsWith(it.key) }?.value?.let { Success(it) }
            ?: Failure(Exception("timeout"))
    }

    private fun filteredLogs(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        appInstance: Long?,
    ) = logs
        .filter { it.dbName == dbName }
        .filter { showTransactions || !it.isTransaction }
        .filter { appInstance == null || it.appInstance == appInstance }
        .filter { log -> filters.all { log.sqlQuery.contains(it.text) || log.bindArgs.orEmpty().any { arg -> arg.contains(it.text) } } }

    override suspend fun getLatestQueryLogs(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        appInstance: Long?,
        limit: Int,
    ): List<DatabaseQueryLogDomainModel> = filteredLogs(dbName, showTransactions, filters, appInstance)
        .sortedByDescending { it.timestamp }
        .take(limit)

    override suspend fun getQueryLogs(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<DatabaseQueryLogDomainModel> = error("not used")

    override suspend fun registerDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databases: List<DeviceDataBaseDomainModel>) =
        error("not used")

    override suspend fun saveSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId, query: String) =
        error("not used")

    override fun observeLastSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId): Flow<List<String>> =
        error("not used")

    override suspend fun saveTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId, table: DatabaseTableDomainModel) =
        error("not used")

    override suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>,
    ) = error("not used")

    override suspend fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId): Flow<List<DatabaseTableDomainModel>> =
        error("not used")

    override suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String,
    ): Either<Throwable, Unit> = error("not used")

    override suspend fun deleteFavorite(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: String, id: Long): Either<Throwable, Unit> =
        error("not used")

    override fun observeFavorites(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DatabaseFavoriteQueryDomainModel>> = flowOf(emptyList())

    override suspend fun getFavorite(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: String, id: Long): DatabaseFavoriteQueryDomainModel? =
        error("not used")

    override fun observeQueryLogs(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>> = error("not used")

    override fun countQueryLogs(
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<Int> = error("not used")

    override suspend fun deleteAllQueryLogs(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = error("not used")
}
