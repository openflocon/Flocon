package io.github.openflocon.data.core.commands

import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.coroutines.flow.Flow

interface AdbCommandLocalDataSource {

    suspend fun insertOrReplace(adbCommand: AdbCommand)

    suspend fun delete(adbCommand: AdbCommand)

    suspend fun getById(id: Long): AdbCommand?

    fun getAll(): Flow<List<AdbCommand>>
}
