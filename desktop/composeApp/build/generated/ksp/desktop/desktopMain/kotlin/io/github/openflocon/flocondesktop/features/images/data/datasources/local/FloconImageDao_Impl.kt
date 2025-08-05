package io.github.openflocon.flocondesktop.features.images.`data`.datasources.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.features.images.`data`.datasources.local.model.DeviceImageEntity
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
public class FloconImageDao_Impl(
  __db: RoomDatabase,
) : FloconImageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDeviceImageEntity: EntityInsertAdapter<DeviceImageEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDeviceImageEntity = object : EntityInsertAdapter<DeviceImageEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `DeviceImageEntity` (`deviceId`,`url`,`time`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DeviceImageEntity) {
        statement.bindText(1, entity.deviceId)
        statement.bindText(2, entity.url)
        statement.bindLong(3, entity.time)
      }
    }
  }

  public override suspend fun insertImage(image: DeviceImageEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDeviceImageEntity.insert(_connection, image)
  }

  public override fun observeImagesForDevice(deviceId: String): Flow<List<DeviceImageEntity>> {
    val _sql: String = "SELECT * FROM DeviceImageEntity WHERE deviceId = ? ORDER BY time DESC"
    return createFlow(__db, false, arrayOf("DeviceImageEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _result: MutableList<DeviceImageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeviceImageEntity
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTime: Long
          _tmpTime = _stmt.getLong(_columnIndexOfTime)
          _item = DeviceImageEntity(_tmpDeviceId,_tmpUrl,_tmpTime)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getImagesForDevice(deviceId: String): List<DeviceImageEntity> {
    val _sql: String = "SELECT * FROM DeviceImageEntity WHERE deviceId = ? ORDER BY time DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _result: MutableList<DeviceImageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeviceImageEntity
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTime: Long
          _tmpTime = _stmt.getLong(_columnIndexOfTime)
          _item = DeviceImageEntity(_tmpDeviceId,_tmpUrl,_tmpTime)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllImagesForDevice(deviceId: String) {
    val _sql: String = "DELETE FROM DeviceImageEntity WHERE deviceId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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
