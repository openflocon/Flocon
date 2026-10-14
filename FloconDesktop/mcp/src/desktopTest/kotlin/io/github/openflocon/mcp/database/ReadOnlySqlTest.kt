package io.github.openflocon.mcp.database

import io.github.openflocon.mcp.McpToolException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ReadOnlySqlTest {

    @Test
    fun `read only queries are accepted`() {
        listOf(
            "SELECT * FROM users",
            "  select id, name from users where name = 'a;b' ; ",
            "SELECT * FROM users WHERE name = 'it''s; DROP TABLE users'",
            "SELECT \"weird;column\" FROM [my;table] -- comment; DELETE FROM users",
            "SELECT 1 /* ; DELETE FROM users */",
            "EXPLAIN QUERY PLAN SELECT * FROM users",
            "PRAGMA user_version",
            "PRAGMA table_info(users)",
            "pragma main.table_info('users')",
            "PRAGMA index_list(\"users\")",
        ).forEach { checkReadOnlySql(it) }
    }

    @Test
    fun `modifying queries are refused`() {
        listOf(
            "DELETE FROM users",
            "UPDATE users SET name = 'a'",
            "INSERT INTO users VALUES (1)",
            "DROP TABLE users",
            "SELECT * FROM users; DELETE FROM users",
            "SELECT 1;;DROP TABLE users",
            "WITH a AS (SELECT 1) SELECT * FROM a",
            "PRAGMA user_version = 3",
            "PRAGMA user_version(3)",
            "PRAGMA journal_mode=DELETE",
            "-- comment\nDELETE FROM users",
            "",
            "  ;  ",
        ).forEach { sql ->
            assertFailsWith<McpToolException>(sql) { checkReadOnlySql(sql) }
        }
    }
}
