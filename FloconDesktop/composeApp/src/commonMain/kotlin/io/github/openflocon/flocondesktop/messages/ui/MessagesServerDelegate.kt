package io.github.openflocon.flocondesktop.messages.ui

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.messages.usecase.HandleIncomingMessagesUseCase
import io.github.openflocon.domain.messages.usecase.HandleReceivedFilesUseCase
import io.github.openflocon.domain.messages.usecase.StartServerUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MessagesServerDelegate(
    private val startServerUseCase: StartServerUseCase,
    private val handleIncomingMessagesUseCase: HandleIncomingMessagesUseCase,
    private val handleReceivedFilesUseCase: HandleReceivedFilesUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val dispatcherProvider: DispatcherProvider,
) : CloseableScoped by closeableDelegate {

    private val _serverError = MutableStateFlow<String?>(null)
    val serverError = _serverError.asStateFlow()

    fun initialize() {
        coroutineScope.launch {
            handleIncomingMessagesUseCase()
                .collect()
        }

        coroutineScope.launch {
            handleReceivedFilesUseCase()
                .collect()
        }

        // try to start the server
        // if fails -> try again in 3s
        // if success, just re-check again in 20s if it's still alive
        coroutineScope.launch {
            while (isActive) {
                startServer().fold(
                    doOnSuccess = {
                        _serverError.value = null
                        delay(20.seconds)
                    },
                    doOnFailure = {
                        delay(3.seconds)
                    },
                )
            }
        }
    }

    fun relaunchServer() {
        coroutineScope.launch {
            startServer().fold(
                doOnSuccess = {
                    _serverError.value = null
                },
                doOnFailure = {
                    // error is set inside startServer
                }
            )
        }
    }

    private fun startServer(): Either<Throwable, Unit> = try {
        startServerUseCase()
        Success(Unit)
    } catch (t: Throwable) {
        val errorMsg = buildString {
            append("Cannot start server on port ${Constant.SERVER_WEBSOCKET_PORT}")
            t.message?.let {
                append(" : ")
                append(it)
            }
        }
        _serverError.value = errorMsg
        Failure(t)
    }
}
