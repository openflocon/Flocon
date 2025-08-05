package io.github.openflocon.flocondesktop.features.analytics.`data`.datasource.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.common.db.converters.ListStringsConverters
import io.github.openflocon.flocondesktop.features.analytics.`data`.datasource.local.model.AnalyticsItemEntity
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
public class FloconAnalyticsDao_Impl(
  __db: RoomDatabase,
) : FloconAnalyticsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAnalyticsItemEntity: EntityInsertAdapter<AnalyticsItemEntity>

  private val __listStringsConverters: ListStringsConverters = ListStringsConverters()
  init {
    this.__db = __db
    this.__insertAdapterOfAnalyticsItemEntity = object : EntityInsertAdapter<AnalyticsItemEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `AnalyticsItemEntity` (`itemId`,`analyticsTableId`,`deviceId`,`createdAt`,`eventName`,`propertiesColumnsNames`,`propertiesValues`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AnalyticsItemEntity) {
        statement.bindText(1, entity.itemId)
        statement.bindText(2, entity.analyticsTableId)
        statement.bindText(3, entity.deviceId)
        statement.bindLong(4, entity.createdAt)
        statement.bindText(5, entity.eventName)
        val _tmp: String? = __listStringsConverters.toStringList(entity.propertiesColumnsNames)
        if (_tmp == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmp)
        }
        val _tmp_1: String? = __listStringsConverters.toStringList(entity.propertiesValues)
        if (_tmp_1 == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp_1)
        }
      }
    }
  }

  public override suspend
      fun insertAnalyticsItems(analyticsItemEntities: List<AnalyticsItemEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAnalyticsItemEntity.insert(_connection, analyticsItemEntities)
  }

  public override fun observeAnalytics(deviceId: String, analyticsTableId: String):
      Flow<AnalyticsItemEntity?> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM AnalyticsItemEntity 
        |        WHERE deviceId = ?
        |        AND analyticsTableId = ? 
        |        LIMIT 1
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("AnalyticsItemEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, analyticsTableId)
        val _columnIndexOfItemId: Int = getColumnIndexOrThrow(_stmt, "itemId")
        val _columnIndexOfAnalyticsTableId: Int = getColumnIndexOrThrow(_stmt, "analyticsTableId")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfPropertiesColumnsNames: Int = getColumnIndexOrThrow(_stmt,
            "propertiesColumnsNames")
        val _columnIndexOfPropertiesValues: Int = getColumnIndexOrThrow(_stmt, "propertiesValues")
        val _result: AnalyticsItemEntity?
        if (_stmt.step()) {
          val _tmpItemId: String
          _tmpItemId = _stmt.getText(_columnIndexOfItemId)
          val _tmpAnalyticsTableId: String
          _tmpAnalyticsTableId = _stmt.getText(_columnIndexOfAnalyticsTableId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpPropertiesColumnsNames: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfPropertiesColumnsNames)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfPropertiesColumnsNames)
          }
          val _tmp_1: List<String>? = __listStringsConverters.fromStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpPropertiesColumnsNames = _tmp_1
          }
          val _tmpPropertiesValues: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfPropertiesValues)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfPropertiesValues)
          }
          val _tmp_3: List<String>? = __listStringsConverters.fromStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpPropertiesValues = _tmp_3
          }
          _result =
              AnalyticsItemEntity(_tmpItemId,_tmpAnalyticsTableId,_tmpDeviceId,_tmpCreatedAt,_tmpEventName,_tmpPropertiesColumnsNames,_tmpPropertiesValues)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAnalyticsTableIdsForDevice(deviceId: String): Flow<List<String>> {
    val _sql: String = """
        |
        |        SELECT DISTINCT analyticsTableId 
        |        FROM AnalyticsItemEntity 
        |        WHERE deviceId = ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("AnalyticsItemEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAnalyticsForDevice(deviceId: String): List<String> {
    val _sql: String = """
        |
        |        SELECT DISTINCT analyticsTableId 
        |        FROM AnalyticsItemEntity 
        |        WHERE deviceId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAnalyticsItems(deviceId: String, analyticsTableId: String):
      Flow<List<AnalyticsItemEntity>> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM AnalyticsItemEntity 
        |        WHERE analyticsTableId = ? 
        |        AND deviceId = ?
        |        ORDER BY createdAt ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("AnalyticsItemEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, analyticsTableId)
        _argIndex = 2
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfItemId: Int = getColumnIndexOrThrow(_stmt, "itemId")
        val _columnIndexOfAnalyticsTableId: Int = getColumnIndexOrThrow(_stmt, "analyticsTableId")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfEventName: Int = getColumnIndexOrThrow(_stmt, "eventName")
        val _columnIndexOfPropertiesColumnsNames: Int = getColumnIndexOrThrow(_stmt,
            "propertiesColumnsNames")
        val _columnIndexOfPropertiesValues: Int = getColumnIndexOrThrow(_stmt, "propertiesValues")
        val _result: MutableList<AnalyticsItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AnalyticsItemEntity
          val _tmpItemId: String
          _tmpItemId = _stmt.getText(_columnIndexOfItemId)
          val _tmpAnalyticsTableId: String
          _tmpAnalyticsTableId = _stmt.getText(_columnIndexOfAnalyticsTableId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpEventName: String
          _tmpEventName = _stmt.getText(_columnIndexOfEventName)
          val _tmpPropertiesColumnsNames: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfPropertiesColumnsNames)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfPropertiesColumnsNames)
          }
          val _tmp_1: List<String>? = __listStringsConverters.fromStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpPropertiesColumnsNames = _tmp_1
          }
          val _tmpPropertiesValues: List<String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfPropertiesValues)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfPropertiesValues)
          }
          val _tmp_3: List<String>? = __listStringsConverters.fromStringList(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpPropertiesValues = _tmp_3
          }
          _item =
              AnalyticsItemEntity(_tmpItemId,_tmpAnalyticsTableId,_tmpDeviceId,_tmpCreatedAt,_tmpEventName,_tmpPropertiesColumnsNames,_tmpPropertiesValues)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAnalyticsContent(deviceId: String, analyticsTableId: String) {
    val _sql: String = """
        |
        |        DELETE FROM AnalyticsItemEntity
        |        WHERE analyticsTableId = ?
        |        AND deviceId = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, analyticsTableId)
        _argIndex = 2
        _stmt.bindText(_argIndex, deviceId)
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
