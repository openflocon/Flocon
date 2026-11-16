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

    @Test
    fun `room 2 nested transactions are closed by their last end`() {
        val begin = state.onQuery("BEGIN EXCLUSIVE TRANSACTION")
        assertEquals(begin, state.onQuery("BEGIN EXCLUSIVE TRANSACTION"))
        assertEquals(begin, state.onQuery("INSERT INTO user VALUES (1)"))
        assertEquals(begin, state.onQuery("TRANSACTION SUCCESSFUL"))
        assertEquals(begin, state.onQuery("END TRANSACTION"))
        // the outer transaction goes on
        assertEquals(begin, state.onQuery("INSERT INTO post VALUES (1)"))
        assertEquals(begin, state.onQuery("TRANSACTION SUCCESSFUL"))
        assertEquals(begin, state.onQuery("END TRANSACTION"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `room 3 statements run in the order Room really sends them`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")
        assertEquals(begin, state.onQuery("SAVEPOINT 'sp_1'"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (1)"))
        assertEquals(begin, state.onQuery("ROLLBACK TRANSACTION TO SAVEPOINT 'sp_1'"))
        assertEquals(begin, state.onQuery("RELEASE SAVEPOINT 'sp_1'"))
        assertEquals(begin, state.onQuery("INSERT INTO dog VALUES (2)"))
        assertEquals(begin, state.onQuery("END TRANSACTION"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `a room 3 rollback ends the transaction`() {
        val begin = state.onQuery("BEGIN IMMEDIATE TRANSACTION")

        assertEquals(begin, state.onQuery("ROLLBACK TRANSACTION"))
        assertNull(state.onQuery("SELECT 1"))
    }

    @Test
    fun `a query of the outermost transaction has no nested transaction`() {
        val begin = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        val insert = state.track("INSERT INTO dog VALUES (1)")!!

        assertEquals(FloconTransactionInfo(transactionId = begin.transactionId), begin)
        assertEquals(begin, insert)
    }

    @Test
    fun `a savepoint is a transaction nested in the outermost one`() {
        val begin = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        val savepoint = state.track("SAVEPOINT 's1'")!!
        val insert = state.track("INSERT INTO user VALUES (1)")!!
        val release = state.track("RELEASE SAVEPOINT 's1'")!!
        val after = state.track("INSERT INTO post VALUES (1)")!!

        assertNotNull(savepoint.nestedTransactionId)
        assertEquals(begin.transactionId, savepoint.transactionId)
        assertEquals(begin.transactionId, savepoint.parentTransactionId)
        assertEquals(1, savepoint.depth)
        // the savepoint statements and what they hold share the nested id
        assertEquals(savepoint, insert)
        assertEquals(savepoint, release)
        // back in the outermost transaction
        assertEquals(FloconTransactionInfo(transactionId = begin.transactionId), after)
    }

    @Test
    fun `two savepoints in a row are two nested transactions`() {
        state.track("BEGIN IMMEDIATE TRANSACTION")
        val first = state.track("SAVEPOINT 'a'")!!
        state.track("RELEASE SAVEPOINT 'a'")
        val second = state.track("SAVEPOINT 'a'")!!

        assertNotEquals(first.nestedTransactionId, second.nestedTransactionId)
        assertEquals(first.parentTransactionId, second.parentTransactionId)
    }

    @Test
    fun `a savepoint in a savepoint has the first one as parent`() {
        val begin = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        val outer = state.track("SAVEPOINT 'a'")!!
        val inner = state.track("SAVEPOINT 'b'")!!
        val innerInsert = state.track("INSERT INTO dog VALUES (1)")!!
        val innerRelease = state.track("RELEASE SAVEPOINT 'b'")!!
        val outerInsert = state.track("INSERT INTO dog VALUES (2)")!!
        val outerRelease = state.track("RELEASE SAVEPOINT 'a'")!!

        assertEquals(outer.nestedTransactionId, inner.parentTransactionId)
        assertEquals(2, inner.depth)
        assertEquals(inner, innerInsert)
        assertEquals(inner, innerRelease)
        assertEquals(outer, outerInsert)
        assertEquals(outer, outerRelease)
        assertEquals(begin.transactionId, inner.transactionId)
    }

    @Test
    fun `a rollback to a savepoint ends the nested transaction`() {
        state.track("BEGIN IMMEDIATE TRANSACTION")
        val savepoint = state.track("SAVEPOINT 's1'")!!

        assertEquals(savepoint, state.track("ROLLBACK TRANSACTION TO SAVEPOINT 's1'"))
        assertEquals(null, state.track("INSERT INTO dog VALUES (1)")!!.nestedTransactionId)
    }

    @Test
    fun `the end of the outermost transaction closes the nested ones still open`() {
        val begin = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        val savepoint = state.track("SAVEPOINT 's1'")!!

        assertEquals(savepoint, state.track("END TRANSACTION"))
        assertNull(state.track("SELECT 1"))
        // a new transaction does not inherit them
        val next = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        assertNotEquals(begin.transactionId, next.transactionId)
        assertNull(next.nestedTransactionId)
    }

    @Test
    fun `a nested begin of room 2 is a nested transaction ended by its own end`() {
        val begin = state.track("BEGIN EXCLUSIVE TRANSACTION")!!
        val nestedBegin = state.track("BEGIN EXCLUSIVE TRANSACTION")!!
        val insert = state.track("INSERT INTO user VALUES (1)")!!
        val nestedEnd = state.track("END TRANSACTION")!!
        val outerInsert = state.track("INSERT INTO post VALUES (1)")!!
        val end = state.track("END TRANSACTION")!!

        assertEquals(begin.transactionId, nestedBegin.transactionId)
        assertEquals(begin.transactionId, nestedBegin.parentTransactionId)
        assertEquals(nestedBegin, insert)
        assertEquals(nestedBegin, nestedEnd)
        assertEquals(FloconTransactionInfo(transactionId = begin.transactionId), outerInsert)
        assertEquals(FloconTransactionInfo(transactionId = begin.transactionId), end)
        assertNull(state.track("SELECT 1"))
    }

    @Test
    fun `the sample of nested transactions gives each block its own nested transaction`() {
        // transaction { transaction { insertUsers() } transaction { insertPosts() insertComments() } }
        val begin = state.track("BEGIN IMMEDIATE TRANSACTION")!!
        val users = state.track("SAVEPOINT 'sp1'")!!
        assertEquals(users, state.track("INSERT INTO user VALUES (1)"))
        state.track("RELEASE SAVEPOINT 'sp1'")
        val posts = state.track("SAVEPOINT 'sp2'")!!
        assertEquals(posts, state.track("INSERT INTO post VALUES (1)"))
        assertEquals(posts, state.track("INSERT INTO comment VALUES (1)"))
        state.track("RELEASE SAVEPOINT 'sp2'")
        state.track("END TRANSACTION")

        assertNotEquals(users.nestedTransactionId, posts.nestedTransactionId)
        assertEquals(begin.transactionId, users.parentTransactionId)
        assertEquals(begin.transactionId, posts.parentTransactionId)
    }
}
