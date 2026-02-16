package io.github.openflocon.domain.commands.repository

import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.coroutines.flow.Flow

interface AdbCommandRepository {

    suspend fun insertOrUpdate(adbCommand: AdbCommand)

    suspend fun delete(adbCommand: AdbCommand)

    suspend fun get(id: Long): AdbCommand?

    fun getAll(): Flow<List<AdbCommand>>
}
