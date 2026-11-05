package io.github.openflocon.flocon.database.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FloconTransactionStateTest {

    private val state = FloconTransactionState()

    @Test
    fun `a query outside of a transaction has no id`() {
        assertNull(state.onQuery("SELECT * FROM dog"))
    }

    @Test
    fun `all the queries of a transaction share its id, begin and commit included`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        val insert = state.onQuery("INSERT INTO dog VALUES (?)")
        val commit = state.onQuery("COMMIT")

        assertNotNull(begin)
        assertEquals(begin, insert)
        assertEquals(begin, commit)
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `room 2 transactions end with END TRANSACTION`() {
        val begin = state.onQuery("BEGIN EXCLUSIVE TRANSACTION")

        assertEquals(begin, state.onQuery("UPDATE dog SET age = 1"))
        assertEquals(begin, state.onQuery("TRANSACTION SUCCESSFUL"))
        assertEquals(begin, state.onQuery("END TRANSACTION"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `two transactions have different ids`() {
        val first = state.onQuery("BEGIN")
        state.onQuery("COMMIT")
        val second = state.onQuery("BEGIN")

        assertNotEquals(first, second)
    }

    @Test
    fun `a rollback ends the transaction`() {
        val begin = state.onQuery("BEGIN")

        assertEquals(begin, state.onQuery("ROLLBACK"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `a savepoint inside a transaction does not end it`() {
        val begin = state.onQuery("BEGIN")

        assertEquals(begin, state.onQuery("SAVEPOINT sp1"))
        assertEquals(begin, state.onQuery("ROLLBACK TO SAVEPOINT sp1"))
        assertEquals(begin, state.onQuery("RELEASE SAVEPOINT sp1"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(begin, state.onQuery("COMMIT"))
    }

    @Test
    fun `a savepoint outside of a transaction is a transaction until it is released`() {
        val savepoint = state.onQuery("SAVEPOINT sp1")

        assertNotNull(savepoint)
        assertEquals(savepoint, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(savepoint, state.onQuery("RELEASE sp1"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `the statements are recognized whatever their case and leading spaces`() {
        val begin = state.onQuery("  begin transaction")

        assertEquals(begin, state.onQuery("commit"))
        assertNull(state.onQuery("SELECT 1"))
    }
}
