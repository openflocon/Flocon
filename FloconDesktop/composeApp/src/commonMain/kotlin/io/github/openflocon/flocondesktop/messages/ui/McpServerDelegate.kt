package io.github.openflocon.flocondesktop.messages.ui

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.mcp.usecase.StartMcpServerUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Starts the MCP server used by AI agents (Claude Code, Cursor, Codex...) to inspect Flocon data
 */
class McpServerDelegate(
    private val startMcpServerUseCase: StartMcpServerUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
) : CloseableScoped by closeableDelegate {

    fun initialize() {
        // try to start the server, if it fails (ex: port already used) -> try again later
        coroutineScope.launch {
            var errorDisplayed = false
            while (isActive) {
                startServer(displayError = !errorDisplayed).fold(
                    doOnSuccess = {
                        return@launch
                    },
                    doOnFailure = {
                        errorDisplayed = true
                        delay(10.seconds)
                    },
                )
            }
        }
    }

    private fun startServer(displayError: Boolean): Either<Throwable, Unit> = try {
        startMcpServerUseCase()
        Success(Unit)
    } catch (t: Throwable) {
        if (displayError) {
            feedbackDisplayer.displayMessage(
                buildString {
                    append("Cannot start MCP server on port ${Constant.MCP_SERVER_PORT}")
                    t.message?.let {
                        append(" : ")
                        append(it)
                    }
                },
                type = FeedbackDisplayer.MessageType.Error,
            )
        }
        Failure(t)
    }
}
