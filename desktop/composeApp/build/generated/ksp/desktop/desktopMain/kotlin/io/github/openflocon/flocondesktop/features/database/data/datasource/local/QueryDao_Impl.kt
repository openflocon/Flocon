package io.github.openflocon.flocondesktop.features.database.`data`.datasource.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
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
public class QueryDao_Impl(
  __db: RoomDatabase,
) : QueryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSuccessQueryEntity: EntityInsertAdapter<SuccessQueryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSuccessQueryEntity = object : EntityInsertAdapter<SuccessQueryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `SuccessQueryEntity` (`id`,`deviceId`,`databaseId`,`queryString`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SuccessQueryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.deviceId)
        statement.bindText(3, entity.databaseId)
        statement.bindText(4, entity.queryString)
        statement.bindLong(5, entity.timestamp)
      }
    }
  }

  public override suspend fun insertSuccessQuery(query: SuccessQueryEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSuccessQueryEntity.insert(_connection, query)
  }

  public override fun observeSuccessQueriesByDeviceId(deviceId: String, databaseId: String):
      Flow<List<String>> {
    val _sql: String = """
        |
        |        SELECT DISTINCT queryString
        |        FROM SuccessQueryEntity 
        |        WHERE deviceId = ? 
        |        AND databaseId = ? 
        |        ORDER BY timestamp DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("SuccessQueryEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, databaseId)
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

  public override suspend fun doesQueryExists(
    deviceId: String,
    databaseId: String,
    queryString: String,
  ): Boolean {
    val _sql: String = """
        |
        |        SELECT EXISTS(
        |            SELECT 1
        |            FROM SuccessQueryEntity 
        |            WHERE queryString = ? 
        |            AND databaseId = ? 
        |            AND deviceId = ? 
        |        )
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, queryString)
        _argIndex = 2
        _stmt.bindText(_argIndex, databaseId)
        _argIndex = 3
        _stmt.bindText(_argIndex, deviceId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteQuery(
    deviceId: String,
    databaseId: String,
    queryString: String,
  ) {
    val _sql: String = """
        |
        |        DELETE FROM SuccessQueryEntity 
        |        WHERE deviceId = ? 
        |        AND databaseId = ?
        |        AND queryString = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, databaseId)
        _argIndex = 3
        _stmt.bindText(_argIndex, queryString)
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
