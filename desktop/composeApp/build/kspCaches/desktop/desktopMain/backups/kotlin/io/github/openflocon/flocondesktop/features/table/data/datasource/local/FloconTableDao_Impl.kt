package io.github.openflocon.flocondesktop.features.table.`data`.datasource.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.common.db.converters.ListStringsConverters
import io.github.openflocon.flocondesktop.features.table.`data`.datasource.local.model.TableEntity
import io.github.openflocon.flocondesktop.features.table.`data`.datasource.local.model.TableItemEntity
import javax.`annotation`.processing.Generated
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
public class FloconTableDao_Impl(
  __db: RoomDatabase,
) : FloconTableDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTableEntity: EntityInsertAdapter<TableEntity>

  private val __insertAdapterOfTableItemEntity: EntityInsertAdapter<TableItemEntity>

  private val __listStringsConverters: ListStringsConverters = ListStringsConverters()
  init {
    this.__db = __db
    this.__insertAdapterOfTableEntity = object : EntityInsertAdapter<TableEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `TableEntity` (`id`,`deviceId`,`name`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TableEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.deviceId)
        statement.bindText(3, entity.name)
      }
    }
    this.__insertAdapterOfTableItemEntity = object : EntityInsertAdapter<TableItemEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `TableItemEntity` (`itemId`,`tableId`,`createdAt`,`columnsNames`,`values`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TableItemEntity) {
        statement.bindText(1, entity.itemId)
        statement.bindLong(2, entity.tableId)
        statement.bindLong(3, entity.createdAt)
        val _tmp: String? = __listStringsConverters.toStringList(entity.columnsNames)
        if (_tmp == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmp)
        }
        val _tmp_1: String? = __listStringsConverters.toStringList(entity.values)
        if (_tmp_1 == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmp_1)
        }
      }
    }
  }

  public override suspend fun insertTable(table: TableEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfTableEntity.insertAndReturnId(_connection, table)
    _result
  }

  public override suspend fun insertTableItems(tableItemEntities: List<TableItemEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTableItemEntity.insert(_connection, tableItemEntities)
  }

  public override suspend fun getTableId(deviceId: String, tableName: String): Long? {
    val _sql: String = """
        |
        |        SELECT id
        |        FROM TableEntity 
        |        WHERE deviceId = ? AND name = ? LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, tableName)
        val _result: Long?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTable(deviceId: String, tableId: Long): Flow<TableEntity?> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM TableEntity 
        |        WHERE deviceId = ? AND id = ? LIMIT 1
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("TableEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, tableId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _result: TableEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          _result = TableEntity(_tmpId,_tmpDeviceId,_tmpName)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTablesForDevice(deviceId: String): Flow<List<TableEntity>> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM TableEntity 
        |        WHERE deviceId = ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("TableEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _result: MutableList<TableEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TableEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          _item = TableEntity(_tmpId,_tmpDeviceId,_tmpName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTablesForDevice(deviceId: String): List<TableEntity> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM TableEntity 
        |        WHERE deviceId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _result: MutableList<TableEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TableEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          _item = TableEntity(_tmpId,_tmpDeviceId,_tmpName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTableItems(tableId: Long): Flow<List<TableItemEntity>> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM TableItemEntity 
        |        WHERE tableId = ? 
        |        ORDER BY createdAt ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("TableItemEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, tableId)
        val _columnIndexOfItemId: Int = getColumnIndexOrThrow(_stmt, "itemId")
        val _columnIndexOfTableId: Int = getColumnIndexOrThrow(_stmt, "tableId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfColumnsNames: Int = getColumnIndexOrThrow(_stmt, "columnsNames")
        val _columnIndexOfValues: Int = getColumnIndexOrThrow(_stmt, "values")
        val _result: MutableList<TableItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TableItemEntity
          val _tmpItemId: String
          _tmpItemId = _stmt.getText(_columnIndexOfItemId)
          val _tmpTableId: Long
          _tmpTableId = _stmt.getLong(_columnIndexOfTableId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpColumnsNames: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfColumnsNames)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfColumnsNames)
          }
          val _tmp_1: List<String>? = __listStringsConverters.fromStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpColumnsNames = _tmp_1
          }
          val _tmpValues: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfValues)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfValues)
          }
          val _tmp_3: List<String>? = __listStringsConverters.fromStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpValues = _tmp_3
          }
          _item = TableItemEntity(_tmpItemId,_tmpTableId,_tmpCreatedAt,_tmpColumnsNames,_tmpValues)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTableContent(tableId: Long) {
    val _sql: String = """
        |
        |        DELETE FROM TableItemEntity
        |        WHERE tableId = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, tableId)
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
