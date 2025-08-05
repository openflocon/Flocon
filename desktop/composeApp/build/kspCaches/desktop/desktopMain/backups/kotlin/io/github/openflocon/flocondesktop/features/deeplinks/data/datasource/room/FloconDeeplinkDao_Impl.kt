package io.github.openflocon.flocondesktop.features.deeplinks.`data`.datasource.room

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.features.deeplinks.`data`.datasource.room.model.DeeplinkEntity
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
public class FloconDeeplinkDao_Impl(
  __db: RoomDatabase,
) : FloconDeeplinkDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDeeplinkEntity: EntityInsertAdapter<DeeplinkEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDeeplinkEntity = object : EntityInsertAdapter<DeeplinkEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `DeeplinkEntity` (`id`,`deviceId`,`link`,`label`,`description`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DeeplinkEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.deviceId)
        statement.bindText(3, entity.link)
        val _tmpLabel: String? = entity.label
        if (_tmpLabel == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLabel)
        }
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDescription)
        }
      }
    }
  }

  public override suspend fun insert(deeplink: DeeplinkEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDeeplinkEntity.insert(_connection, deeplink)
  }

  public override suspend fun updateAll(deviceId: String, deeplinks: List<DeeplinkEntity>): Unit =
      performInTransactionSuspending(__db) {
    super@FloconDeeplinkDao_Impl.updateAll(deviceId, deeplinks)
  }

  public override fun observeAll(deviceId: String): Flow<List<DeeplinkEntity>> {
    val _sql: String = """
        |
        |            SELECT *
        |            FROM DeeplinkEntity
        |            WHERE deviceId = ?
        |            ORDER BY id ASC
        |            
        """.trimMargin()
    return createFlow(__db, false, arrayOf("DeeplinkEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<DeeplinkEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeeplinkEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpLink: String
          _tmpLink = _stmt.getText(_columnIndexOfLink)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          _item = DeeplinkEntity(_tmpId,_tmpDeviceId,_tmpLink,_tmpLabel,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll(deviceId: String) {
    val _sql: String = """
        |
        |       DELETE FROM DeeplinkEntity
        |       WHERE deviceId = ? 
        |    
        """.trimMargin()
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
