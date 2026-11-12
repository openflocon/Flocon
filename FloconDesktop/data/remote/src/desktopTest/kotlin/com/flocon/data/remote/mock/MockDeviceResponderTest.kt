package com.flocon.data.remote.mock

import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class MockDeviceResponderTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val device = scenario("s") {
        android("Pixel 8", app = "App", packageName = "a.b") {
            databases {
                database("db") {
                    table("users") {
                        column("id", "INTEGER", primaryKey = true)
                        column("name", "TEXT")
                        row(1, "Jane")
                        row(2, "Marc")
                    }
                }
            }
            sharedPreferences {
                preferences("p") { string("k", "v") }
            }
            files {
                root("files", "Files", "/data/files") { file("a.txt", 10, 1.minutes.ago) }
            }
        }
    }.devices.single()

    private fun responder() = MockDeviceResponder(device, Instant.fromEpochMilliseconds(1_800_000_000_000), json)

    private fun query(sql: String): String {
        val body = """{"query":${Json.encodeToString(String.serializer(), sql)},"requestId":"r1","database":"db"}"""
        val reply = responder().respond(FloconOutgoingMessageDataModel("database", "query", body)).single() as MockReply.Message
        return reply.body
    }

    @Test
    fun `lists the tables`() {
        assertTrue(query("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name").contains("users"))
    }

    @Test
    fun `selects the rows with a limit`() {
        val body = query("SELECT * FROM users LIMIT 1 OFFSET 1")
        assertTrue(body.contains("Marc"))
        assertTrue(!body.contains("Jane"))
    }

    @Test
    fun `unsupported sql is an error`() {
        assertTrue(query("DROP TABLE users").contains("Error"))
    }

    @Test
    fun `removes a preference key`() {
        val responder = responder()
        val reply = responder.respond(
            FloconOutgoingMessageDataModel("shared_preferences", "remove_shared_preference_value", """{"requestId":"r","sharedPreferenceName":"p","key":"k"}"""),
        ).single() as MockReply.Message
        assertEquals("get_shared_preference_value", reply.method)
        assertTrue(!reply.body.contains("\"k\""))
    }

    @Test
    fun `lists a root by its id`() {
        val reply = responder().respond(
            FloconOutgoingMessageDataModel("files", "list_files", """{"requestId":"r","path":"files","isConstantPath":true,"withFoldersSize":false}"""),
        ).single() as MockReply.Message
        assertTrue(reply.body.contains("/data/files/a.txt"))
    }
}
