package io.github.openflocon.flocondesktop.features.network.`data`.datasource.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.common.db.converters.MapStringsConverters
import io.github.openflocon.flocondesktop.features.network.`data`.datasource.local.model.FloconHttpRequestEntity
import io.github.openflocon.flocondesktop.features.network.`data`.datasource.local.model.FloconHttpRequestInfosEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FloconHttpRequestDao_Impl(
  __db: RoomDatabase,
) : FloconHttpRequestDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFloconHttpRequestEntity: EntityInsertAdapter<FloconHttpRequestEntity>

  private val __mapStringsConverters: MapStringsConverters = MapStringsConverters()
  init {
    this.__db = __db
    this.__insertAdapterOfFloconHttpRequestEntity = object :
        EntityInsertAdapter<FloconHttpRequestEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `FloconHttpRequestEntity` (`uuid`,`deviceId`,`url`,`method`,`startTime`,`durationMs`,`requestHeaders`,`requestBody`,`requestByteSize`,`responseContentType`,`responseBody`,`responseHeaders`,`responseByteSize`,`graphql_query`,`graphql_operationType`,`graphql_isSuccess`,`graphql_responseHttpCode`,`http_responseHttpCode`,`grpc_responseStatus`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FloconHttpRequestEntity) {
        statement.bindText(1, entity.uuid)
        statement.bindText(2, entity.deviceId)
        val _tmpInfos: FloconHttpRequestInfosEntity = entity.infos
        statement.bindText(3, _tmpInfos.url)
        statement.bindText(4, _tmpInfos.method)
        statement.bindLong(5, _tmpInfos.startTime)
        statement.bindDouble(6, _tmpInfos.durationMs)
        val _tmp: String? = __mapStringsConverters.toStringMap(_tmpInfos.requestHeaders)
        if (_tmp == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp)
        }
        val _tmpRequestBody: String? = _tmpInfos.requestBody
        if (_tmpRequestBody == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRequestBody)
        }
        statement.bindLong(9, _tmpInfos.requestByteSize)
        val _tmpResponseContentType: String? = _tmpInfos.responseContentType
        if (_tmpResponseContentType == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpResponseContentType)
        }
        val _tmpResponseBody: String? = _tmpInfos.responseBody
        if (_tmpResponseBody == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpResponseBody)
        }
        val _tmp_1: String? = __mapStringsConverters.toStringMap(_tmpInfos.responseHeaders)
        if (_tmp_1 == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmp_1)
        }
        statement.bindLong(13, _tmpInfos.responseByteSize)
        val _tmpGraphql: FloconHttpRequestEntity.GraphQlEmbedded? = entity.graphql
        if (_tmpGraphql != null) {
          statement.bindText(14, _tmpGraphql.query)
          statement.bindText(15, _tmpGraphql.operationType)
          val _tmp_2: Int = if (_tmpGraphql.isSuccess) 1 else 0
          statement.bindLong(16, _tmp_2.toLong())
          statement.bindLong(17, _tmpGraphql.responseHttpCode.toLong())
        } else {
          statement.bindNull(14)
          statement.bindNull(15)
          statement.bindNull(16)
          statement.bindNull(17)
        }
        val _tmpHttp: FloconHttpRequestEntity.HttpEmbedded? = entity.http
        if (_tmpHttp != null) {
          statement.bindLong(18, _tmpHttp.responseHttpCode.toLong())
        } else {
          statement.bindNull(18)
        }
        val _tmpGrpc: FloconHttpRequestEntity.GrpcEmbedded? = entity.grpc
        if (_tmpGrpc != null) {
          statement.bindText(19, _tmpGrpc.responseStatus)
        } else {
          statement.bindNull(19)
        }
      }
    }
  }

  public override suspend fun upsertRequest(request: FloconHttpRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFloconHttpRequestEntity.insert(_connection, request)
  }

  public override fun observeRequests(deviceId: String): Flow<List<FloconHttpRequestEntity>> {
    val _sql: String = """
        |
        |        SELECT * 
        |        FROM FloconHttpRequestEntity 
        |        WHERE deviceId = ? 
        |        ORDER BY startTime ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("FloconHttpRequestEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        val _columnIndexOfUuid: Int = getColumnIndexOrThrow(_stmt, "uuid")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfRequestHeaders: Int = getColumnIndexOrThrow(_stmt, "requestHeaders")
        val _columnIndexOfRequestBody: Int = getColumnIndexOrThrow(_stmt, "requestBody")
        val _columnIndexOfRequestByteSize: Int = getColumnIndexOrThrow(_stmt, "requestByteSize")
        val _columnIndexOfResponseContentType: Int = getColumnIndexOrThrow(_stmt,
            "responseContentType")
        val _columnIndexOfResponseBody: Int = getColumnIndexOrThrow(_stmt, "responseBody")
        val _columnIndexOfResponseHeaders: Int = getColumnIndexOrThrow(_stmt, "responseHeaders")
        val _columnIndexOfResponseByteSize: Int = getColumnIndexOrThrow(_stmt, "responseByteSize")
        val _columnIndexOfQuery: Int = getColumnIndexOrThrow(_stmt, "graphql_query")
        val _columnIndexOfOperationType: Int = getColumnIndexOrThrow(_stmt, "graphql_operationType")
        val _columnIndexOfIsSuccess: Int = getColumnIndexOrThrow(_stmt, "graphql_isSuccess")
        val _columnIndexOfResponseHttpCode: Int = getColumnIndexOrThrow(_stmt,
            "graphql_responseHttpCode")
        val _columnIndexOfResponseHttpCode_1: Int = getColumnIndexOrThrow(_stmt,
            "http_responseHttpCode")
        val _columnIndexOfResponseStatus: Int = getColumnIndexOrThrow(_stmt, "grpc_responseStatus")
        val _result: MutableList<FloconHttpRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FloconHttpRequestEntity
          val _tmpUuid: String
          _tmpUuid = _stmt.getText(_columnIndexOfUuid)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpInfos: FloconHttpRequestInfosEntity
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpDurationMs: Double
          _tmpDurationMs = _stmt.getDouble(_columnIndexOfDurationMs)
          val _tmpRequestHeaders: Map<String, String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfRequestHeaders)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfRequestHeaders)
          }
          val _tmp_1: Map<String, String>? = __mapStringsConverters.fromStringMap(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.Map<kotlin.String, kotlin.String>', but it was NULL.")
          } else {
            _tmpRequestHeaders = _tmp_1
          }
          val _tmpRequestBody: String?
          if (_stmt.isNull(_columnIndexOfRequestBody)) {
            _tmpRequestBody = null
          } else {
            _tmpRequestBody = _stmt.getText(_columnIndexOfRequestBody)
          }
          val _tmpRequestByteSize: Long
          _tmpRequestByteSize = _stmt.getLong(_columnIndexOfRequestByteSize)
          val _tmpResponseContentType: String?
          if (_stmt.isNull(_columnIndexOfResponseContentType)) {
            _tmpResponseContentType = null
          } else {
            _tmpResponseContentType = _stmt.getText(_columnIndexOfResponseContentType)
          }
          val _tmpResponseBody: String?
          if (_stmt.isNull(_columnIndexOfResponseBody)) {
            _tmpResponseBody = null
          } else {
            _tmpResponseBody = _stmt.getText(_columnIndexOfResponseBody)
          }
          val _tmpResponseHeaders: Map<String, String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfResponseHeaders)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfResponseHeaders)
          }
          val _tmp_3: Map<String, String>? = __mapStringsConverters.fromStringMap(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.Map<kotlin.String, kotlin.String>', but it was NULL.")
          } else {
            _tmpResponseHeaders = _tmp_3
          }
          val _tmpResponseByteSize: Long
          _tmpResponseByteSize = _stmt.getLong(_columnIndexOfResponseByteSize)
          _tmpInfos =
              FloconHttpRequestInfosEntity(_tmpUrl,_tmpMethod,_tmpStartTime,_tmpDurationMs,_tmpRequestHeaders,_tmpRequestBody,_tmpRequestByteSize,_tmpResponseContentType,_tmpResponseBody,_tmpResponseHeaders,_tmpResponseByteSize)
          val _tmpGraphql: FloconHttpRequestEntity.GraphQlEmbedded?
          if (!(_stmt.isNull(_columnIndexOfQuery) && _stmt.isNull(_columnIndexOfOperationType) &&
              _stmt.isNull(_columnIndexOfIsSuccess) &&
              _stmt.isNull(_columnIndexOfResponseHttpCode))) {
            val _tmpQuery: String
            _tmpQuery = _stmt.getText(_columnIndexOfQuery)
            val _tmpOperationType: String
            _tmpOperationType = _stmt.getText(_columnIndexOfOperationType)
            val _tmpIsSuccess: Boolean
            val _tmp_4: Int
            _tmp_4 = _stmt.getLong(_columnIndexOfIsSuccess).toInt()
            _tmpIsSuccess = _tmp_4 != 0
            val _tmpResponseHttpCode: Int
            _tmpResponseHttpCode = _stmt.getLong(_columnIndexOfResponseHttpCode).toInt()
            _tmpGraphql =
                FloconHttpRequestEntity.GraphQlEmbedded(_tmpQuery,_tmpOperationType,_tmpIsSuccess,_tmpResponseHttpCode)
          } else {
            _tmpGraphql = null
          }
          val _tmpHttp: FloconHttpRequestEntity.HttpEmbedded?
          if (!(_stmt.isNull(_columnIndexOfResponseHttpCode_1))) {
            val _tmpResponseHttpCode_1: Int
            _tmpResponseHttpCode_1 = _stmt.getLong(_columnIndexOfResponseHttpCode_1).toInt()
            _tmpHttp = FloconHttpRequestEntity.HttpEmbedded(_tmpResponseHttpCode_1)
          } else {
            _tmpHttp = null
          }
          val _tmpGrpc: FloconHttpRequestEntity.GrpcEmbedded?
          if (!(_stmt.isNull(_columnIndexOfResponseStatus))) {
            val _tmpResponseStatus: String
            _tmpResponseStatus = _stmt.getText(_columnIndexOfResponseStatus)
            _tmpGrpc = FloconHttpRequestEntity.GrpcEmbedded(_tmpResponseStatus)
          } else {
            _tmpGrpc = null
          }
          _item =
              FloconHttpRequestEntity(_tmpUuid,_tmpDeviceId,_tmpInfos,_tmpGraphql,_tmpHttp,_tmpGrpc)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRequestById(deviceId: String, requestId: String):
      Flow<FloconHttpRequestEntity?> {
    val _sql: String = """
        |
        |        SELECT *
        |        FROM FloconHttpRequestEntity
        |        WHERE deviceId = ? AND uuid = ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("FloconHttpRequestEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, requestId)
        val _columnIndexOfUuid: Int = getColumnIndexOrThrow(_stmt, "uuid")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfRequestHeaders: Int = getColumnIndexOrThrow(_stmt, "requestHeaders")
        val _columnIndexOfRequestBody: Int = getColumnIndexOrThrow(_stmt, "requestBody")
        val _columnIndexOfRequestByteSize: Int = getColumnIndexOrThrow(_stmt, "requestByteSize")
        val _columnIndexOfResponseContentType: Int = getColumnIndexOrThrow(_stmt,
            "responseContentType")
        val _columnIndexOfResponseBody: Int = getColumnIndexOrThrow(_stmt, "responseBody")
        val _columnIndexOfResponseHeaders: Int = getColumnIndexOrThrow(_stmt, "responseHeaders")
        val _columnIndexOfResponseByteSize: Int = getColumnIndexOrThrow(_stmt, "responseByteSize")
        val _columnIndexOfQuery: Int = getColumnIndexOrThrow(_stmt, "graphql_query")
        val _columnIndexOfOperationType: Int = getColumnIndexOrThrow(_stmt, "graphql_operationType")
        val _columnIndexOfIsSuccess: Int = getColumnIndexOrThrow(_stmt, "graphql_isSuccess")
        val _columnIndexOfResponseHttpCode: Int = getColumnIndexOrThrow(_stmt,
            "graphql_responseHttpCode")
        val _columnIndexOfResponseHttpCode_1: Int = getColumnIndexOrThrow(_stmt,
            "http_responseHttpCode")
        val _columnIndexOfResponseStatus: Int = getColumnIndexOrThrow(_stmt, "grpc_responseStatus")
        val _result: FloconHttpRequestEntity?
        if (_stmt.step()) {
          val _tmpUuid: String
          _tmpUuid = _stmt.getText(_columnIndexOfUuid)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          val _tmpInfos: FloconHttpRequestInfosEntity
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpDurationMs: Double
          _tmpDurationMs = _stmt.getDouble(_columnIndexOfDurationMs)
          val _tmpRequestHeaders: Map<String, String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfRequestHeaders)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfRequestHeaders)
          }
          val _tmp_1: Map<String, String>? = __mapStringsConverters.fromStringMap(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.Map<kotlin.String, kotlin.String>', but it was NULL.")
          } else {
            _tmpRequestHeaders = _tmp_1
          }
          val _tmpRequestBody: String?
          if (_stmt.isNull(_columnIndexOfRequestBody)) {
            _tmpRequestBody = null
          } else {
            _tmpRequestBody = _stmt.getText(_columnIndexOfRequestBody)
          }
          val _tmpRequestByteSize: Long
          _tmpRequestByteSize = _stmt.getLong(_columnIndexOfRequestByteSize)
          val _tmpResponseContentType: String?
          if (_stmt.isNull(_columnIndexOfResponseContentType)) {
            _tmpResponseContentType = null
          } else {
            _tmpResponseContentType = _stmt.getText(_columnIndexOfResponseContentType)
          }
          val _tmpResponseBody: String?
          if (_stmt.isNull(_columnIndexOfResponseBody)) {
            _tmpResponseBody = null
          } else {
            _tmpResponseBody = _stmt.getText(_columnIndexOfResponseBody)
          }
          val _tmpResponseHeaders: Map<String, String>
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfResponseHeaders)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfResponseHeaders)
          }
          val _tmp_3: Map<String, String>? = __mapStringsConverters.fromStringMap(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'kotlin.collections.Map<kotlin.String, kotlin.String>', but it was NULL.")
          } else {
            _tmpResponseHeaders = _tmp_3
          }
          val _tmpResponseByteSize: Long
          _tmpResponseByteSize = _stmt.getLong(_columnIndexOfResponseByteSize)
          _tmpInfos =
              FloconHttpRequestInfosEntity(_tmpUrl,_tmpMethod,_tmpStartTime,_tmpDurationMs,_tmpRequestHeaders,_tmpRequestBody,_tmpRequestByteSize,_tmpResponseContentType,_tmpResponseBody,_tmpResponseHeaders,_tmpResponseByteSize)
          val _tmpGraphql: FloconHttpRequestEntity.GraphQlEmbedded?
          if (!(_stmt.isNull(_columnIndexOfQuery) && _stmt.isNull(_columnIndexOfOperationType) &&
              _stmt.isNull(_columnIndexOfIsSuccess) &&
              _stmt.isNull(_columnIndexOfResponseHttpCode))) {
            val _tmpQuery: String
            _tmpQuery = _stmt.getText(_columnIndexOfQuery)
            val _tmpOperationType: String
            _tmpOperationType = _stmt.getText(_columnIndexOfOperationType)
            val _tmpIsSuccess: Boolean
            val _tmp_4: Int
            _tmp_4 = _stmt.getLong(_columnIndexOfIsSuccess).toInt()
            _tmpIsSuccess = _tmp_4 != 0
            val _tmpResponseHttpCode: Int
            _tmpResponseHttpCode = _stmt.getLong(_columnIndexOfResponseHttpCode).toInt()
            _tmpGraphql =
                FloconHttpRequestEntity.GraphQlEmbedded(_tmpQuery,_tmpOperationType,_tmpIsSuccess,_tmpResponseHttpCode)
          } else {
            _tmpGraphql = null
          }
          val _tmpHttp: FloconHttpRequestEntity.HttpEmbedded?
          if (!(_stmt.isNull(_columnIndexOfResponseHttpCode_1))) {
            val _tmpResponseHttpCode_1: Int
            _tmpResponseHttpCode_1 = _stmt.getLong(_columnIndexOfResponseHttpCode_1).toInt()
            _tmpHttp = FloconHttpRequestEntity.HttpEmbedded(_tmpResponseHttpCode_1)
          } else {
            _tmpHttp = null
          }
          val _tmpGrpc: FloconHttpRequestEntity.GrpcEmbedded?
          if (!(_stmt.isNull(_columnIndexOfResponseStatus))) {
            val _tmpResponseStatus: String
            _tmpResponseStatus = _stmt.getText(_columnIndexOfResponseStatus)
            _tmpGrpc = FloconHttpRequestEntity.GrpcEmbedded(_tmpResponseStatus)
          } else {
            _tmpGrpc = null
          }
          _result =
              FloconHttpRequestEntity(_tmpUuid,_tmpDeviceId,_tmpInfos,_tmpGraphql,_tmpHttp,_tmpGrpc)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM FloconHttpRequestEntity"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDeviceCalls(deviceId: String) {
    val _sql: String = """
        |
        |        DELETE FROM FloconHttpRequestEntity
        |        WHERE deviceId = ?
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

  public override suspend fun deleteRequest(deviceId: String, requestId: String) {
    val _sql: String = """
        |
        |        DELETE FROM FloconHttpRequestEntity
        |        WHERE deviceId = ?
        |        AND uuid = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, requestId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteRequestBefore(deviceId: String, requestId: String) {
    val _sql: String = """
        |
        |        DELETE FROM FloconHttpRequestEntity
        |        WHERE deviceId = ?
        |        AND startTime < (
        |            SELECT startTime 
        |            FROM FloconHttpRequestEntity 
        |            WHERE uuid = ? 
        |            AND deviceId = ?
        |            LIMIT 1
        |        )
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, requestId)
        _argIndex = 3
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
