package io.github.openflocon.data.local.files.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import io.github.openflocon.data.local.files.models.FileEntity
import io.github.openflocon.data.local.files.models.FileOptionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconFileDao {
    @Query(
        """
        SELECT * 
        FROM FileEntity 
        WHERE deviceId = :deviceId 
        AND parentPath = :parentFilePath
        AND packageName = :packageName
    """,
    )
    fun observeFolderContent(
        deviceId: String,
        packageName: String,
        parentFilePath: String,
    ): Flow<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileEntity>)

    @Query(
        """
        DELETE FROM FileEntity 
        WHERE deviceId = :deviceId 
        AND parentPath = :parentPath
        AND packageName = :packageName
    """,
    )
    suspend fun clearFolderContent(
        deviceId: String,
        packageName: String,
        parentPath: String,
    )

    @Upsert
    suspend fun updateFileOptions(option: FileOptionsEntity)

    @Query(
        """
        SELECT * 
        FROM FileOptionsEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
        """
    )
    fun observeFileOptions(
        deviceId: String,
        packageName: String,
    ): Flow<FileOptionsEntity?>
}
