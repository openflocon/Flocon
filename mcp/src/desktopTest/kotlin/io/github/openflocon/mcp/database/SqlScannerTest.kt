package io.github.openflocon.mcp.database

import kotlin.test.Test
import kotlin.test.assertEquals

class SqlScannerTest {

    /**
     * @return the code chars and the quoted chars
     */
    private fun String.scan(): Pair<String, String> {
        val code = StringBuilder()
        val quoted = StringBuilder()
        forEachOutsideQuotes(onQuoted = { quoted.append(it) }, onCode = { code.append(it) })
        // each char is visited exactly once
        assertEquals(length, code.length + quoted.length, this)
        return code.toString() to quoted.toString()
    }

    @Test
    fun `code without quotes`() {
        assertEquals("SELECT * FROM users" to "", "SELECT * FROM users".scan())
    }

    @Test
    fun `strings and identifiers are quoted, delimiters included`() {
        assertEquals("SELECT  FROM t" to "'a;b'", "SELECT 'a;b' FROM t".scan())
        assertEquals("SELECT  FROM t" to "\"my;column\"", "SELECT \"my;column\" FROM t".scan())
        assertEquals("SELECT  FROM t" to "`my;column`", "SELECT `my;column` FROM t".scan())
        assertEquals("SELECT * FROM " to "[my;table]", "SELECT * FROM [my;table]".scan())
    }

    @Test
    fun `an escaped quote stays in the string`() {
        assertEquals("WHERE name = " to "'it''s; ok'", "WHERE name = 'it''s; ok'".scan())
    }

    @Test
    fun `line comments end with the line`() {
        assertEquals("SELECT 1 FROM t" to "-- x;y\n", "SELECT 1 -- x;y\nFROM t".scan())
        assertEquals("SELECT 1 " to "-- no line break;", "SELECT 1 -- no line break;".scan())
    }

    @Test
    fun `block comments`() {
        assertEquals("SELECT  1" to "/* ; */", "SELECT /* ; */ 1".scan())
        // the closing */ can't reuse the * of the opening /*
        assertEquals("SELECT  2" to "/*/ 1 */", "SELECT /*/ 1 */ 2".scan())
    }

    @Test
    fun `quotes inside a comment and comments inside a string are ignored`() {
        assertEquals(" SELECT " to "/* 'x */';'", "/* 'x */ SELECT ';'".scan())
        assertEquals("SELECT  , x" to "'-- [ /*'", "SELECT '-- [ /*' , x".scan())
    }

    @Test
    fun `an unclosed quote or comment lasts until the end`() {
        assertEquals("SELECT " to "'abc; DELETE", "SELECT 'abc; DELETE".scan())
        assertEquals("SELECT 1 " to "/* x; DELETE", "SELECT 1 /* x; DELETE".scan())
        assertEquals("SELECT " to "[t", "SELECT [t".scan())
    }

    @Test
    fun `statements are split on the semicolons outside quotes`() {
        assertEquals(listOf("SELECT 1", " SELECT 2"), "SELECT 1; SELECT 2".splitStatements())
        // the quoted parts and the comments stay in their statement
        assertEquals(listOf("SELECT ';' FROM t -- ;"), "SELECT ';' FROM t -- ;".splitStatements())
        assertEquals(listOf("SELECT 1"), "SELECT 1;;  ;".splitStatements())
        assertEquals(emptyList(), "  ;  ".splitStatements())
    }

    @Test
    fun `quoted parts are removed`() {
        assertEquals("PRAGMA table_info()", "PRAGMA table_info('a=b')".withoutQuotedParts())
        assertEquals("PRAGMA user_version  = 3", "PRAGMA user_version /* = */ = 3".withoutQuotedParts())
    }
}
