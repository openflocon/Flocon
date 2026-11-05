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

    @Test
    fun `commit, rollback and release outside of a transaction have no id`() {
        assertNull(state.onQuery("COMMIT"))
        assertNull(state.onQuery("ROLLBACK"))
        assertNull(state.onQuery("RELEASE sp1"))
        assertNull(state.onQuery("END TRANSACTION"))
        assertNull(state.onQuery("select 1"))
    }

    @Test
    fun `a nested begin stays in the same transaction`() {
        val begin = state.onQuery("BEGIN EXCLUSIVE TRANSACTION")

        assertEquals(begin, state.onQuery("BEGIN DEFERRED TRANSACTION"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
    }

    @Test
    fun `several transactions in a row all have their own id`() {
        val ids = (1..5).map {
            val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
            assertEquals(begin, state.onQuery("INSERT INTO dog VALUES ($it)"))
            assertEquals(begin, state.onQuery("COMMIT"))
            assertNull(state.onQuery("SELECT 1"))
            begin
        }

        assertEquals(5, ids.toSet().size)
    }

    @Test
    fun `transactions inside a transaction share the id of the outer one`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")

        assertEquals(begin, state.onQuery("SAVEPOINT s1"))
        assertEquals(begin, state.onQuery("INSERT INTO user VALUES (1)"))
        assertEquals(begin, state.onQuery("RELEASE s1"))
        assertEquals(begin, state.onQuery("SAVEPOINT s2"))
        assertEquals(begin, state.onQuery("INSERT INTO post VALUES (1)"))
        assertEquals(begin, state.onQuery("INSERT INTO comment VALUES (1)"))
        assertEquals(begin, state.onQuery("RELEASE s2"))
        assertEquals(begin, state.onQuery("COMMIT"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `releasing the last savepoint of an outer transaction does not end it`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        state.onQuery("SAVEPOINT s1")
        state.onQuery("RELEASE s1")

        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(begin, state.onQuery("COMMIT"))
    }

    @Test
    fun `deeply nested savepoints are all in the same transaction`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")

        assertEquals(begin, state.onQuery("SAVEPOINT a"))
        assertEquals(begin, state.onQuery("SAVEPOINT b"))
        assertEquals(begin, state.onQuery("SAVEPOINT c"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(begin, state.onQuery("RELEASE c"))
        assertEquals(begin, state.onQuery("RELEASE b"))
        assertEquals(begin, state.onQuery("RELEASE a"))
        assertEquals(begin, state.onQuery("UPDATE dog SET age = 2"))
        assertEquals(begin, state.onQuery("COMMIT"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `nested savepoints without an outer transaction end with the outermost one`() {
        val outer = state.onQuery("SAVEPOINT outer_sp")

        assertNotNull(outer)
        assertEquals(outer, state.onQuery("SAVEPOINT inner_sp"))
        assertEquals(outer, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(outer, state.onQuery("RELEASE inner_sp"))
        // the outer one is still open
        assertEquals(outer, state.onQuery("INSERT INTO dog VALUES (2)"))
        assertEquals(outer, state.onQuery("RELEASE outer_sp"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `sibling savepoints without an outer transaction are two transactions`() {
        val first = state.onQuery("SAVEPOINT s1")
        assertEquals(first, state.onQuery("INSERT INTO user VALUES (1)"))
        assertEquals(first, state.onQuery("RELEASE s1"))

        val second = state.onQuery("SAVEPOINT s2")
        assertEquals(second, state.onQuery("INSERT INTO post VALUES (1)"))
        assertEquals(second, state.onQuery("RELEASE s2"))

        assertNotNull(first)
        assertNotNull(second)
        assertNotEquals(first, second)
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `a rollback to a savepoint keeps the transaction open`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        state.onQuery("SAVEPOINT s1")
        state.onQuery("INSERT INTO dog VALUES (1)")

        assertEquals(begin, state.onQuery("ROLLBACK TO s1"))
        assertEquals(begin, state.onQuery("RELEASE s1"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (2)"))
        assertEquals(begin, state.onQuery("COMMIT"))
    }

    @Test
    fun `a rollback ends the transaction and its savepoints`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        state.onQuery("SAVEPOINT a")
        state.onQuery("SAVEPOINT b")

        assertEquals(begin, state.onQuery("ROLLBACK"))
        assertNull(state.onQuery("SELECT 1"))

        // the next transaction does not inherit the open savepoints
        val next = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        assertNotEquals(begin, next)
        state.onQuery("SAVEPOINT c")
        state.onQuery("RELEASE c")
        assertEquals(next, state.onQuery("COMMIT"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `a transaction started by a savepoint can follow a committed one`() {
        val first = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        state.onQuery("COMMIT")
        val second = state.onQuery("SAVEPOINT s1")
        assertEquals(second, state.onQuery("RELEASE s1"))
        val third = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        assertEquals(third, state.onQuery("COMMIT"))

        assertEquals(3, setOf(first, second, third).size)
    }

    @Test
    fun `queries between two transactions have no id`() {
        val first = state.onQuery("BEGIN")
        state.onQuery("COMMIT")

        assertNull(state.onQuery("SELECT 1"))
        assertNull(state.onQuery("INSERT INTO dog VALUES (1)"))

        val second = state.onQuery("BEGIN")
        assertEquals(second, state.onQuery("INSERT INTO dog VALUES (2)"))
        assertNotEquals(first, second)
    }

    @Test
    fun `two states do not share their transactions`() {
        val other = FloconTransactionState()

        val begin = state.onQuery("BEGIN")
        val otherBegin = other.onQuery("BEGIN")

        assertNotEquals(begin, otherBegin)
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(otherBegin, other.onQuery("INSERT INTO dog VALUES (1)"))

        state.onQuery("COMMIT")
        assertNull(state.onQuery("SELECT 1"))
        assertEquals(otherBegin, other.onQuery("SELECT 1"))
    }

    @Test
    fun `room 2 nested transaction logs are all in the same transaction`() {
        val begin = state.onQuery("BEGIN EXCLUSIVE TRANSACTION")

        assertEquals(begin, state.onQuery("INSERT INTO user VALUES (1)"))
        assertEquals(begin, state.onQuery("INSERT INTO post VALUES (1)"))
        assertEquals(begin, state.onQuery("TRANSACTION SUCCESSFUL"))
        assertEquals(begin, state.onQuery("END TRANSACTION"))
        assertNull(state.onQuery("SELECT 1"))
    }
}
