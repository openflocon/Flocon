package io.github.openflocon.domain.commands.repository

import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.coroutines.flow.Flow

interface AdbCommandRepository {

    suspend fun insertOrUpdate(adbCommand: AdbCommand)

    suspend fun delete(adbCommand: AdbCommand)

    fun getAll(): Flow<List<AdbCommand>>

}
