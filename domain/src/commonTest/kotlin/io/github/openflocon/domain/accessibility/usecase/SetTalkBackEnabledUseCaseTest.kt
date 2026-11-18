package io.github.openflocon.domain.accessibility.usecase

import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SetTalkBackEnabledUseCaseTest {

    private val calls = mutableListOf<String>()

    private suspend fun run(canToggle: Boolean, canGrant: Boolean = true, grantFails: Boolean = false) = toggleTalkBack(
        canToggle = canToggle,
        canGrant = canGrant,
        grantPermission = {
            calls += "grant"
            if (grantFails) Failure(Throwable("not allowed")) else Success("")
        },
        send = { calls += "send" },
    )

    @Test
    fun `the permission is granted first when the device cannot toggle yet`() = runBlocking {
        val result = run(canToggle = false)

        assertIs<Success<SetTalkBackEnabledResult>>(result)
        assertEquals(SetTalkBackEnabledResult.Sent, result.value)
        assertEquals(listOf("grant", "send"), calls)
    }

    @Test
    fun `nothing is granted when the device can already toggle`() = runBlocking {
        val result = run(canToggle = true)

        assertIs<Success<SetTalkBackEnabledResult>>(result)
        assertEquals(SetTalkBackEnabledResult.Sent, result.value)
        assertEquals(listOf("send"), calls)
    }

    @Test
    fun `over wifi the request is sent without granting when the permission is missing`() = runBlocking {
        val result = run(canToggle = false, canGrant = false)

        assertIs<Success<SetTalkBackEnabledResult>>(result)
        assertEquals(SetTalkBackEnabledResult.SentWithoutPermission, result.value)
        assertEquals(listOf("send"), calls)
    }

    @Test
    fun `over wifi the request is sent when the permission is already granted`() = runBlocking {
        val result = run(canToggle = true, canGrant = false)

        assertIs<Success<SetTalkBackEnabledResult>>(result)
        assertEquals(SetTalkBackEnabledResult.Sent, result.value)
        assertEquals(listOf("send"), calls)
    }

    @Test
    fun `nothing is sent when the permission cannot be granted`() = runBlocking {
        val result = run(canToggle = false, grantFails = true)

        assertIs<Failure<Throwable>>(result)
        assertEquals("not allowed", result.value.message)
        assertEquals(listOf("grant"), calls)
    }
}
