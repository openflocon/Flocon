package io.github.openflocon.flocondesktop.features.files.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.openflocon.flocondesktop.features.files.data.datasources.model.FileEntity
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
}
