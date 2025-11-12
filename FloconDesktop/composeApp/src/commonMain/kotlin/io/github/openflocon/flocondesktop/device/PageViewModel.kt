package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull

abstract class PageViewModel(
    protected val deviceSerial: String,
    private val sendCommandUseCase: SendCommandUseCase
) : ViewModel() {

    protected suspend fun sendCommand(vararg args: String): String {
        return sendCommandUseCase(deviceSerial, *args)
            .getOrNull()
            .orEmpty()
            .removeSuffix("\n")
    }

}
