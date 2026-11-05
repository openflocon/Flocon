package com.flocon.data.remote.database.datasource

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QueryDatabaseRemoteDataSourceImplTest {

    private val server = object : Server {
        override fun startWebsocket(port: Int) = Unit
        override fun starHttp(port: Int) = Unit
        override fun stop() = Unit
        override val receivedMessages: Flow<FloconIncomingMessageDataModel> = emptyFlow()
        override suspend fun sendMessageToClient(
            deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
            message: FloconOutgoingMessageDataModel,
        ) = Unit
        override val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>> = emptyFlow()
        override val receivedFiles: Flow<FloconReceivedFileDomainModel> = emptyFlow()
    }

    private val dataSource = QueryDatabaseRemoteDataSourceImpl(server, Json { ignoreUnknownKeys = true })

    private fun message(body: String) = FloconIncomingMessageDomainModel(
        deviceName = "Pixel 9",
        deviceId = "pixel",
        plugin = "database",
        body = body,
        method = "log_query",
        appName = "Sample",
        appPackageName = "io.flocon.sample",
        appInstance = 7L,
        platform = "android",
        floconVersionOnDevice = "1.0.0",
    )

    @Test
    fun `a log is linked to the database by its id`() {
        val log = dataSource.getQueryLogs(
            message("""{"dbId":"cats (Room 2)","sqlQuery":"SELECT 1","bindArgs":["1"],"timestamp":1000}"""),
        )

        assertNotNull(log)
        assertEquals("cats (Room 2)", log.dbId)
        assertEquals(7L, log.appInstance)
    }

    @Test
    fun `the transactions are still detected`() {
        val log = dataSource.getQueryLogs(
            message("""{"dbId":"cats","sqlQuery":"BEGIN TRANSACTION","bindArgs":null,"timestamp":1000}"""),
        )

        assertTrue(log!!.isTransaction)
    }

    @Test
    fun `the statements of room 2 and room 3 transactions are detected`() {
        listOf(
            "BEGIN EXCLUSIVE TRANSACTION",
            "BEGIN DEFERRED TRANSACTION",
            "BEGIN IMMEDIATE TRANSACTION",
            "begin",
            "END TRANSACTION",
            "SAVEPOINT sp1",
            "RELEASE SAVEPOINT sp1",
            "ROLLBACK TO SAVEPOINT sp1",
            "TRANSACTION SUCCESSFUL",
        ).forEach { sql ->
            assertTrue(isTransactionControl(sql), sql)
        }
    }

    @Test
    fun `the queries of the app are not transactions`() {
        listOf("SELECT * FROM begin_events", "INSERT INTO dog VALUES (1)", "UPDATE commit_log SET a = 1").forEach { sql ->
            assertFalse(isTransactionControl(sql), sql)
        }
    }

    @Test
    fun `the transaction id is read`() {
        val log = dataSource.getQueryLogs(
            message("""{"dbId":"cats","sqlQuery":"COMMIT","bindArgs":null,"timestamp":1000,"transactionId":"tx-1"}"""),
        )

        assertEquals("tx-1", log!!.transactionId)
    }

    @Test
    fun `the transaction id is optional`() {
        val log = dataSource.getQueryLogs(
            message("""{"dbId":"cats","sqlQuery":"SELECT 1","bindArgs":null,"timestamp":1000}"""),
        )

        assertNull(log!!.transactionId)
    }
}
