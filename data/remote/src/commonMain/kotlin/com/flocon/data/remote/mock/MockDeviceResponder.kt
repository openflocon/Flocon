package com.flocon.data.remote.mock

import com.flocon.data.remote.database.models.DatabaseExecuteSqlResponseDataModel
import com.flocon.data.remote.database.models.DatabaseOutgoingQueryMessage
import com.flocon.data.remote.database.models.DeviceDataBaseDataModel
import com.flocon.data.remote.database.models.QueryResultReceivedDataModel
import com.flocon.data.remote.files.models.FromDeviceFilesDataModel
import com.flocon.data.remote.files.models.FromDeviceFilesResultDataModel
import com.flocon.data.remote.files.models.FromDeviceFilesRootsDataModel
import com.flocon.data.remote.files.models.ToDeviceDeleteFileMessage
import com.flocon.data.remote.files.models.ToDeviceDeleteFilesMessage
import com.flocon.data.remote.files.models.ToDeviceDeleteFolderContentMessage
import com.flocon.data.remote.files.models.ToDeviceGetFileMessage
import com.flocon.data.remote.files.models.ToDeviceGetFilesMessage
import com.flocon.data.remote.files.models.ToDeviceGetRootsMessage
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.sharedpreference.mapper.SharedPreferenceValuesResponse
import com.flocon.data.remote.sharedpreference.models.DeviceSharedPreferenceDataModel
import com.flocon.data.remote.sharedpreference.models.ToDeviceEditSharedPreferenceValueMessage
import com.flocon.data.remote.sharedpreference.models.ToDeviceGetSharedPreferenceValueMessage
import com.flocon.data.remote.sharedpreference.models.ToDeviceRemoveSharedPreferenceValueMessage
import io.github.openflocon.domain.Protocol
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import kotlin.time.Instant

/** What a mocked device answers to the desktop. */
internal sealed interface MockReply {
    /** A websocket message of the device. */
    data class Message(val plugin: String, val method: String, val body: String) : MockReply

    /** A file the device uploaded (the answer to `get_file`). */
    data class UploadedFile(val requestId: String, val remotePath: String, val localPath: String) : MockReply
}

/**
 * Answers, for one mocked device, the requests the desktop sends and expects an answer to:
 * the databases (SQL queries), the files, the preferences. The answers come from the data of the scenario.
 * The other commands (network mocks, dashboard clicks, TalkBack...) are ignored.
 */
internal class MockDeviceResponder(
    private val device: MockDevice,
    private val launchTime: Instant,
    private val json: Json,
) {
    private val databases = device.events.filterIsInstance<MockEvent.Databases>().flatMap { it.databases }.associateBy { it.id }

    private val roots: List<MockFileNode> = device.events.filterIsInstance<MockEvent.Files>().flatMap { it.roots }.map { root ->
        MockFileNode(
            name = root.name,
            path = root.path,
            isDirectory = true,
            size = 0,
            lastModified = launchTime.toEpochMilliseconds(),
            content = null,
            id = root.id,
            children = root.children.map { it.toNode(parentPath = root.path) }.toMutableList(),
        )
    }

    private val preferences = device.events.filterIsInstance<MockEvent.SharedPreferences>().flatMap { it.preferences }
        .associate { it.name to it.rows.toMutableList() }

    private var downloadFolder: File? = null

    fun respond(message: FloconOutgoingMessageDataModel): List<MockReply> = try {
        when (message.plugin) {
            Protocol.ToDevice.Database.Plugin -> respondDatabase(message)
            Protocol.ToDevice.Files.Plugin -> respondFiles(message)
            Protocol.ToDevice.SharedPreferences.Plugin -> respondSharedPreferences(message)
            else -> emptyList()
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }

    // region database

    private fun respondDatabase(message: FloconOutgoingMessageDataModel): List<MockReply> = when (message.method) {
        Protocol.ToDevice.Database.Method.GetDatabases -> listOf(
            reply(
                Protocol.FromDevice.Database.Plugin,
                Protocol.FromDevice.Database.Method.GetDatabases,
                json.encodeToString(databases.values.map { DeviceDataBaseDataModel(id = it.id, name = it.name) }),
            ),
        )

        Protocol.ToDevice.Database.Method.Query -> {
            val request = json.decodeFromString<DatabaseOutgoingQueryMessage>(message.body)
            listOf(
                reply(
                    Protocol.FromDevice.Database.Plugin,
                    Protocol.FromDevice.Database.Method.Query,
                    json.encodeToString(
                        QueryResultReceivedDataModel(
                            requestId = request.requestId,
                            result = encodeQueryResult(runQuery(request.database, request.query)),
                        ),
                    ),
                ),
            )
        }

        else -> emptyList()
    }

    /** `{"type":"Select","body":"<the result serialized as a string>"}`, like the SDK sends it. */
    private fun encodeQueryResult(result: DatabaseExecuteSqlResponseDataModel): String {
        val (type, body) = when (result) {
            is DatabaseExecuteSqlResponseDataModel.Select -> "Select" to json.encodeToString(result)
            is DatabaseExecuteSqlResponseDataModel.Insert -> "Insert" to json.encodeToString(result)
            is DatabaseExecuteSqlResponseDataModel.UpdateDelete -> "UpdateDelete" to json.encodeToString(result)
            is DatabaseExecuteSqlResponseDataModel.Error -> "Error" to json.encodeToString(result)
            DatabaseExecuteSqlResponseDataModel.RawSuccess -> "RawSuccess" to "{}"
        }
        return buildJsonObject {
            put("type", type)
            put("body", body)
        }.toString()
    }

    /**
     * A tiny SQL model, enough for what the desktop sends: the list of tables, `PRAGMA table_info`, and
     * `SELECT <columns | * | COUNT(*)> FROM <table> [WHERE col = value] [LIMIT n [OFFSET m]]`. Anything else is an error.
     */
    private fun runQuery(databaseId: String, sql: String): DatabaseExecuteSqlResponseDataModel {
        val database = databases[databaseId] ?: return error(sql, "no such database: $databaseId")
        val statement = sql.trim().trimEnd(';').trim()
        val lower = statement.lowercase()

        if (lower.startsWith("select") && "sqlite_master" in lower) {
            return DatabaseExecuteSqlResponseDataModel.Select(
                columns = listOf("name"),
                values = database.tables.map { it.name }.sorted().map { listOf(it) },
            )
        }

        val pragma = Regex("""pragma\s+table_info\(\s*[`"'\[]?(\w+)[`"'\]]?\s*\)""", RegexOption.IGNORE_CASE).matchEntire(statement)
        if (pragma != null) {
            val table = database.tables.firstOrNull { it.name.equals(pragma.groupValues[1], ignoreCase = true) }
                ?: return error(sql, "no such table: ${pragma.groupValues[1]}")
            return DatabaseExecuteSqlResponseDataModel.Select(
                columns = listOf("cid", "name", "type", "notnull", "dflt_value", "pk"),
                values = table.columns.mapIndexed { index, column ->
                    listOf(index.toString(), column.name, column.type, if (column.nullable) "0" else "1", null, if (column.primaryKey) "1" else "0")
                },
            )
        }

        val select = Regex("""select\s+(.+?)\s+from\s+[`"'\[]?(\w+)[`"'\]]?(.*)""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).matchEntire(statement)
        if (select != null) {
            val (selected, tableName, rest) = select.destructured
            val table = database.tables.firstOrNull { it.name.equals(tableName, ignoreCase = true) }
                ?: return error(sql, "no such table: $tableName")
            return select(table, selected.trim(), rest, sql)
        }

        return error(sql, "Flocon mock mode only answers SELECT queries on the sample tables")
    }

    private fun select(
        table: MockEvent.Databases.Table,
        selected: String,
        rest: String,
        sql: String,
    ): DatabaseExecuteSqlResponseDataModel {
        var rows = table.rows

        Regex("""where\s+[`"']?(\w+)[`"']?\s*=\s*'?([^'\s]+)'?""", RegexOption.IGNORE_CASE).find(rest)?.let { where ->
            val index = table.columns.indexOfFirst { it.name.equals(where.groupValues[1], ignoreCase = true) }
            if (index < 0) return error(sql, "no such column: ${where.groupValues[1]}")
            rows = rows.filter { it.getOrNull(index) == where.groupValues[2] }
        }

        if (selected.lowercase().startsWith("count(")) {
            return DatabaseExecuteSqlResponseDataModel.Select(columns = listOf(selected), values = listOf(listOf(rows.size.toString())))
        }

        Regex("""limit\s+(\d+)(?:\s+offset\s+(\d+))?""", RegexOption.IGNORE_CASE).find(rest)?.let { limit ->
            val offset = limit.groupValues[2].toIntOrNull() ?: 0
            rows = rows.drop(offset).take(limit.groupValues[1].toInt())
        }

        if (selected == "*") {
            return DatabaseExecuteSqlResponseDataModel.Select(columns = table.columns.map { it.name }, values = rows)
        }

        val names = selected.split(',').map { it.trim().trim('`', '"') }
        val indexes = names.map { name -> table.columns.indexOfFirst { it.name.equals(name, ignoreCase = true) } }
        if (indexes.any { it < 0 }) return error(sql, "no such column: ${names[indexes.indexOf(-1)]}")
        return DatabaseExecuteSqlResponseDataModel.Select(columns = names, values = rows.map { row -> indexes.map { row.getOrNull(it) } })
    }

    private fun error(sql: String, message: String) = DatabaseExecuteSqlResponseDataModel.Error(message = "$message (code 1 SQLITE_ERROR)", originalSql = sql)

    // endregion

    // region files

    private class MockFileNode(
        val name: String,
        val path: String,
        val isDirectory: Boolean,
        val size: Long,
        val lastModified: Long,
        val content: String?,
        val children: MutableList<MockFileNode>,
        val id: String? = null, // set for a root folder
    ) {
        fun totalSize(): Long = if (isDirectory) children.sumOf { it.totalSize() } else size
    }

    private fun MockEvent.Files.Entry.toNode(parentPath: String): MockFileNode {
        val path = "$parentPath/$name"
        val modifiedAt = (launchTime - modifiedAgo).toEpochMilliseconds()
        return when (this) {
            is MockEvent.Files.Entry.Folder -> MockFileNode(name, path, true, 0, modifiedAt, null, children.map { it.toNode(path) }.toMutableList())
            is MockEvent.Files.Entry.File -> MockFileNode(name, path, false, size, modifiedAt, content, mutableListOf())
        }
    }

    private fun findNode(path: String): MockFileNode? {
        fun search(node: MockFileNode): MockFileNode? = if (node.path == path) node else node.children.firstNotNullOfOrNull(::search)
        return roots.firstNotNullOfOrNull(::search)
    }

    private fun folder(path: String, isConstantPath: Boolean): MockFileNode? = if (isConstantPath) roots.firstOrNull { it.id == path } else findNode(path)

    private fun respondFiles(message: FloconOutgoingMessageDataModel): List<MockReply> {
        when (message.method) {
            Protocol.ToDevice.Files.Method.GetRoots -> {
                val request = json.decodeFromString<ToDeviceGetRootsMessage>(message.body)
                return listOf(
                    reply(
                        Protocol.FromDevice.Files.Plugin,
                        Protocol.FromDevice.Files.Method.GetRoots,
                        json.encodeToString(
                            FromDeviceFilesRootsDataModel(
                                requestId = request.requestId,
                                roots = roots.map { FromDeviceFilesRootsDataModel.Root(id = it.id ?: it.path, name = it.name, path = it.path) },
                            ),
                        ),
                    ),
                )
            }

            Protocol.ToDevice.Files.Method.ListFiles -> {
                val request = json.decodeFromString<ToDeviceGetFilesMessage>(message.body)
                return listOf(listing(request.requestId, folder(request.path, request.isConstantPath), request.withFoldersSize))
            }

            Protocol.ToDevice.Files.Method.DeleteFile -> {
                val request = json.decodeFromString<ToDeviceDeleteFileMessage>(message.body)
                val parent = folder(request.parentPath, request.isConstantParentPath)
                parent?.children?.removeAll { it.path == request.filePath }
                return listOf(listing(request.requestId, parent, withFoldersSize = true))
            }

            Protocol.ToDevice.Files.Method.DeleteFiles -> {
                val request = json.decodeFromString<ToDeviceDeleteFilesMessage>(message.body)
                val parent = folder(request.parentPath, request.isConstantParentPath)
                parent?.children?.removeAll { it.path in request.filePaths }
                return listOf(listing(request.requestId, parent, withFoldersSize = true))
            }

            Protocol.ToDevice.Files.Method.DeleteFolderContent -> {
                val request = json.decodeFromString<ToDeviceDeleteFolderContentMessage>(message.body)
                val folder = folder(request.path, request.isConstantPath)
                folder?.children?.clear()
                return listOf(listing(request.requestId, folder, withFoldersSize = true))
            }

            Protocol.ToDevice.Files.Method.GetFile -> {
                val request = json.decodeFromString<ToDeviceGetFileMessage>(message.body)
                val node = findNode(request.path)?.takeIf { !it.isDirectory } ?: return emptyList()
                val file = File(downloadFolder ?: kotlin.io.path.createTempDirectory("flocon-mock-files").toFile().also { downloadFolder = it }, node.name)
                file.writeText(node.content ?: "Mock content of ${node.name}\n")
                return listOf(MockReply.UploadedFile(requestId = request.requestId, remotePath = request.path, localPath = file.absolutePath))
            }

            else -> return emptyList()
        }
    }

    private fun listing(requestId: String, folder: MockFileNode?, withFoldersSize: Boolean) = reply(
        Protocol.FromDevice.Files.Plugin,
        Protocol.FromDevice.Files.Method.ListFiles,
        json.encodeToString(
            FromDeviceFilesResultDataModel(
                requestId = requestId,
                files = folder?.children.orEmpty().map {
                    FromDeviceFilesDataModel(
                        name = it.name,
                        isDirectory = it.isDirectory,
                        path = it.path,
                        size = if (it.isDirectory && !withFoldersSize) 0 else it.totalSize(),
                        lastModified = it.lastModified,
                    )
                },
            ),
        ),
    )

    // endregion

    // region shared preferences

    private fun respondSharedPreferences(message: FloconOutgoingMessageDataModel): List<MockReply> {
        when (message.method) {
            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences -> {
                val types = device.events.filterIsInstance<MockEvent.SharedPreferences>().flatMap { it.preferences }.associate { it.name to it.type }
                return listOf(
                    reply(
                        Protocol.FromDevice.SharedPreferences.Plugin,
                        Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
                        json.encodeToString(preferences.keys.sorted().map { DeviceSharedPreferenceDataModel(name = it, type = types[it]) }),
                    ),
                )
            }

            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue -> {
                val request = json.decodeFromString<ToDeviceGetSharedPreferenceValueMessage>(message.body)
                return listOf(values(request.requestId, request.sharedPreferenceName))
            }

            Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue -> {
                val request = json.decodeFromString<ToDeviceEditSharedPreferenceValueMessage>(message.body)
                val value = request.stringValue?.let(MockEvent.SharedPreferences.Value::Str)
                    ?: request.intValue?.let(MockEvent.SharedPreferences.Value::Integer)
                    ?: request.longValue?.let(MockEvent.SharedPreferences.Value::Long)
                    ?: request.floatValue?.let(MockEvent.SharedPreferences.Value::Float)
                    ?: request.booleanValue?.let(MockEvent.SharedPreferences.Value::Bool)
                    ?: request.setStringValue?.let(MockEvent.SharedPreferences.Value::StringSet)
                preferences[request.sharedPreferenceName]?.let { rows ->
                    if (value != null) {
                        val row = MockEvent.SharedPreferences.Row(request.key, value)
                        val index = rows.indexOfFirst { it.key == request.key }
                        if (index >= 0) rows[index] = row else rows += row
                    }
                }
                return listOf(values(request.requestId, request.sharedPreferenceName))
            }

            Protocol.ToDevice.SharedPreferences.Method.RemoveSharedPreferenceValue -> {
                val request = json.decodeFromString<ToDeviceRemoveSharedPreferenceValueMessage>(message.body)
                preferences[request.sharedPreferenceName]?.removeAll { it.key == request.key }
                return listOf(values(request.requestId, request.sharedPreferenceName))
            }

            else -> return emptyList()
        }
    }

    private fun values(requestId: String, name: String) = reply(
        Protocol.FromDevice.SharedPreferences.Plugin,
        Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue,
        json.encodeToString(
            SharedPreferenceValuesResponse(
                requestId = requestId,
                sharedPreferenceName = name,
                rows = preferences[name].orEmpty().map { row ->
                    when (val value = row.value) {
                        is MockEvent.SharedPreferences.Value.Str -> SharedPreferenceValuesResponse.Row(row.key, stringValue = value.value)
                        is MockEvent.SharedPreferences.Value.Integer -> SharedPreferenceValuesResponse.Row(row.key, intValue = value.value)
                        is MockEvent.SharedPreferences.Value.Long -> SharedPreferenceValuesResponse.Row(row.key, longValue = value.value)
                        is MockEvent.SharedPreferences.Value.Float -> SharedPreferenceValuesResponse.Row(row.key, floatValue = value.value)
                        is MockEvent.SharedPreferences.Value.Bool -> SharedPreferenceValuesResponse.Row(row.key, booleanValue = value.value)
                        is MockEvent.SharedPreferences.Value.StringSet -> SharedPreferenceValuesResponse.Row(row.key, setStringValue = value.value)
                    }
                },
            ),
        ),
    )

    // endregion

    private fun reply(plugin: String, method: String, body: String) = MockReply.Message(plugin, method, body)
}
