package io.github.openflocon.data.core.adbcommander.datasource

import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import kotlinx.coroutines.flow.Flow

interface AdbCommanderLocalDataSource {
    fun observeSavedCommands(deviceId: String): Flow<List<AdbCommandDomainModel>>
    suspend fun saveCommand(deviceId: String, command: AdbCommandDomainModel)
    suspend fun deleteSavedCommand(id: Long)
    suspend fun updateSavedCommand(command: AdbCommandDomainModel, deviceId: String)

    fun observeHistory(deviceId: String): Flow<List<AdbCommandHistoryDomainModel>>
    suspend fun addToHistory(deviceId: String, command: String, output: String, isSuccess: Boolean)
    suspend fun clearHistory(deviceId: String)

    fun observeFlows(deviceId: String): Flow<List<AdbFlowDomainModel>>
    suspend fun getFlowWithSteps(flowId: Long): AdbFlowDomainModel?
    suspend fun saveFlow(deviceId: String, flow: AdbFlowDomainModel): Long
    suspend fun deleteFlow(id: Long)
    suspend fun updateFlow(flow: AdbFlowDomainModel, deviceId: String)
}
