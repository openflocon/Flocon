package io.github.openflocon.flocondesktop.features.files.`data`.datasources

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.features.files.`data`.datasources.model.FileEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FloconFileDao_Impl(
  __db: RoomDatabase,
) : FloconFileDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFileEntity: EntityInsertAdapter<FileEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFileEntity = object : EntityInsertAdapter<FileEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `FileEntity` (`id`,`deviceId`,`name`,`isDirectory`,`path`,`parentPath`,`size`,`lastModifiedTimestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FileEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.deviceId)
        statement.bindText(3, entity.name)
        val _tmp: Int = if (entity.isDirectory) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.path)
        statement.bindText(6, entity.parentPath)
        statement.bindLong(7, entity.size)
        statement.bindLong(8, entity.lastModifiedTimestamp)
      }
    }
  }

  public override suspend fun insertFiles(files: List<FileEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFileEntity.insert(_connection, files)
  }

  public override fun observeFolderContent(deviceId: String, parentFilePath: String):
      Flow<List<FileEntity>> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM FileEntity 
        |        WHERE deviceId = ? 
        |        AND parentPath = ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("FileEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, parentFilePath)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfIsDirectory: Int = getColumnIndexOrThrow(_stmt, "isDirectory")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfParentPath: Int = getColumnIndexOrThrow(_stmt, "parentPath")
        val _columnIndexOfSize: Int = getColumnIndexOrThrow(_stmt, "size")
        val _columnIndexOfLastModifiedTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "lastModifiedTimestamp")
        val _result: MutableList<FileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FileEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpIsDirectory: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDirectory).toInt()
          _tmpIsDirectory = _tmp != 0
          val _tmpPath: String
          _tmpPath = _stmt.getText(_columnIndexOfPath)
          val _tmpParentPath: String
          _tmpParentPath = _stmt.getText(_columnIndexOfParentPath)
          val _tmpSize: Long
          _tmpSize = _stmt.getLong(_columnIndexOfSize)
          val _tmpLastModifiedTimestamp: Long
          _tmpLastModifiedTimestamp = _stmt.getLong(_columnIndexOfLastModifiedTimestamp)
          _item =
              FileEntity(_tmpId,_tmpDeviceId,_tmpName,_tmpIsDirectory,_tmpPath,_tmpParentPath,_tmpSize,_tmpLastModifiedTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearFolderContent(deviceId: String, parentPath: String) {
    val _sql: String = """
        |
        |        DELETE FROM FileEntity 
        |        WHERE deviceId = ? 
        |        AND parentPath = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, parentPath)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
