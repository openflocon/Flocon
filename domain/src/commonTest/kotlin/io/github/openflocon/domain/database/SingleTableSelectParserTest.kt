package io.github.openflocon.domain.database

import io.github.openflocon.domain.database.utils.parseSingleTableSelect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SingleTableSelectParserTest {

    @Test
    fun `finds the table of the query of an opened table`() {
        assertEquals("users", parseSingleTableSelect("SELECT * \nFROM users\nLIMIT 50 OFFSET 0"))
    }

    @Test
    fun `accepts a list of columns, clauses, comments and a final semicolon`() {
        val query = """
            -- the premium ones
            select id, "name", `email` from [user list]
            /* join */ where name != 'select join' order by id desc limit 10;
        """.trimIndent()

        assertEquals("user list", parseSingleTableSelect(query))
    }

    @Test
    fun `rejects the results which are not rows of one table`() {
        listOf(
            "SELECT * FROM users u",
            "SELECT * FROM users, posts",
            "SELECT * FROM users JOIN posts ON posts.userId = users.id",
            "SELECT * FROM users LEFT JOIN posts USING (id)",
            "SELECT * FROM users WHERE id = 1 UNION SELECT * FROM admins",
            "SELECT * FROM users WHERE id IN (SELECT userId FROM posts)",
            "SELECT * FROM (SELECT * FROM users)",
            "SELECT * FROM main.users",
            "SELECT name AS id FROM users",
            "SELECT name id FROM users",
            "SELECT count(*) FROM users",
            "SELECT DISTINCT name FROM users",
            "SELECT * FROM users GROUP BY name",
            "SELECT * FROM users; DELETE FROM users",
            "SELECT * FROM users WHERE name = 'not closed",
            "UPDATE users SET name = 'a'",
            "PRAGMA table_info(users)",
            "",
        ).forEach {
            assertNull(parseSingleTableSelect(it), it)
        }
    }
}
