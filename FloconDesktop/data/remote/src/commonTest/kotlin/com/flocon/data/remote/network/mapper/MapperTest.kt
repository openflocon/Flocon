package com.flocon.data.remote.network.mapper

import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import io.github.openflocon.data.core.network.graphql.model.GraphQlExtracted
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MapperTest {

    @Test
    fun `should extract from body`() {
        val decoded = FloconNetworkRequestDataModel(
            url = "https://ourapi.com/graphql",
            requestBody = """
                {
                  "query": "query UserData { user { id name } }",
                  "variables": {}
                }
            """.trimIndent()
        )

        val result: GraphQlExtracted? = extractGraphQl(decoded)

        assertNotNull(result)
        assertIs<GraphQlExtracted.WithBody>(result)
        assertEquals("UserData", result.queryName)
        assertEquals("query", result.operationType)
    }

    @Test
    fun `should extract from url with persisted query`() {
        val decoded = FloconNetworkRequestDataModel(
            url = "https://ourapi.com/graphql?operationName=UserData&variables=%7B%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22abcdef%22%7D%7D",
            requestBody = null
        )

        val result = extractGraphQl(decoded)

        assertNotNull(result)
        assertIs<GraphQlExtracted.PersistedQuery>(result)
        assertEquals("UserData", result.queryName)
        assertEquals("persistedQuery", result.operationType)
    }

    @Test
    fun `should not extract from url with wrong persisted query`() {
        val decoded = FloconNetworkRequestDataModel(
            // here I replaced "operationName" by "operation"
            url = "https://ourapi.com/graphql?operation=UserData&variables=%7B%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22abcdef%22%7D%7D",
            requestBody = null
        )

        val result = extractGraphQl(decoded)

        assertNull(result)
    }

    @Test
    fun `should return null for invalid request`() {
        val decoded = FloconNetworkRequestDataModel(
            url = "https://ourapi.com/graphql",
            requestBody = "{ invalid json"
        )

        val result = extractGraphQl(decoded)

        assertNull(result)
    }

    @Test
    fun `should extract from url when variable value contains ampersand`() {
        val decoded = FloconNetworkRequestDataModel(
            url = "https://www.ourapi.com/graphql?operationName=UserData&variables=%7B%22var1%22%3A%2212345%266789%22%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22abcdef%22%7D%7D",
            requestBody = null,
        )

        val result = extractGraphQl(decoded)

        assertNotNull(result)
        assertIs<GraphQlExtracted.PersistedQuery>(result)
        assertEquals("UserData", result.queryName)
        assertEquals("persistedQuery", result.operationType)
    }
}
