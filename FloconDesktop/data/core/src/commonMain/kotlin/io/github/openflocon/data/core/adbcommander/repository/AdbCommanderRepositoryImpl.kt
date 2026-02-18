package io.github.openflocon.data.core.adbcommander.repository

import io.github.openflocon.data.core.adbcommander.datasource.AdbCommanderLocalDataSource
import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.common.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AdbCommanderRepositoryImpl(
    private val localDataSource: AdbCommanderLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : AdbCommanderRepository {

    override fun observeSavedCommands(deviceId: String): Flow<List<AdbCommandDomainModel>> =
        localDataSource.observeSavedCommands(deviceId).flowOn(dispatcherProvider.data)

    override suspend fun saveCommand(deviceId: String, command: AdbCommandDomainModel) =
        withContext(dispatcherProvider.data) {
            localDataSource.saveCommand(deviceId, command)
        }

    override suspend fun deleteSavedCommand(id: Long) = withContext(dispatcherProvider.data) {
        localDataSource.deleteSavedCommand(id)
    }

    override suspend fun updateSavedCommand(deviceId: String, command: AdbCommandDomainModel) =
        withContext(dispatcherProvider.data) {
            localDataSource.updateSavedCommand(command, deviceId)
        }

    override fun observeHistory(deviceId: String): Flow<List<AdbCommandHistoryDomainModel>> =
        localDataSource.observeHistory(deviceId).flowOn(dispatcherProvider.data)

    override suspend fun addToHistory(
        deviceId: String,
        command: String,
        output: String,
        isSuccess: Boolean,
    ) = withContext(dispatcherProvider.data) {
        localDataSource.addToHistory(deviceId, command, output, isSuccess)
    }

    override suspend fun clearHistory(deviceId: String) = withContext(dispatcherProvider.data) {
        localDataSource.clearHistory(deviceId)
    }

    override fun observeFlows(deviceId: String): Flow<List<AdbFlowDomainModel>> =
        localDataSource.observeFlows(deviceId).flowOn(dispatcherProvider.data)

    override suspend fun getFlowWithSteps(flowId: Long): AdbFlowDomainModel? =
        withContext(dispatcherProvider.data) {
            localDataSource.getFlowWithSteps(flowId)
        }

    override suspend fun saveFlow(deviceId: String, flow: AdbFlowDomainModel): Long =
        withContext(dispatcherProvider.data) {
            localDataSource.saveFlow(deviceId, flow)
        }

    override suspend fun deleteFlow(id: Long) = withContext(dispatcherProvider.data) {
        localDataSource.deleteFlow(id)
    }

    override suspend fun updateFlow(deviceId: String, flow: AdbFlowDomainModel) =
        withContext(dispatcherProvider.data) {
            localDataSource.updateFlow(flow, deviceId)
        }
}
