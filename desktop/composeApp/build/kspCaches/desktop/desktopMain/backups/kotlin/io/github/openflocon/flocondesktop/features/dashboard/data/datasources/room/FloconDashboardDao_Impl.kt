package io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room

import androidx.collection.LongSparseArray
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.room.util.recursiveFetchMap
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementButton
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementCheckBox
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementEntity
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementPlainText
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementText
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardElementTextField
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardEntity
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.model.DashboardSectionEntity
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FloconDashboardDao_Impl(
  __db: RoomDatabase,
) : FloconDashboardDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDashboardEntity: EntityInsertAdapter<DashboardEntity>

  private val __insertAdapterOfDashboardSectionEntity: EntityInsertAdapter<DashboardSectionEntity>

  private val __insertAdapterOfDashboardElementEntity: EntityInsertAdapter<DashboardElementEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDashboardEntity = object : EntityInsertAdapter<DashboardEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `DashboardEntity` (`dashboardId`,`deviceId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DashboardEntity) {
        statement.bindText(1, entity.dashboardId)
        statement.bindText(2, entity.deviceId)
      }
    }
    this.__insertAdapterOfDashboardSectionEntity = object :
        EntityInsertAdapter<DashboardSectionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `DashboardSectionEntity` (`id`,`dashboardId`,`sectionOrder`,`name`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DashboardSectionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.dashboardId)
        statement.bindLong(3, entity.sectionOrder.toLong())
        statement.bindText(4, entity.name)
      }
    }
    this.__insertAdapterOfDashboardElementEntity = object :
        EntityInsertAdapter<DashboardElementEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `DashboardElementEntity` (`id`,`sectionId`,`elementOrder`,`button_text`,`button_actionId`,`text_label`,`text_value`,`text_color`,`plainText_label`,`plainText_value`,`plainText_type`,`textField_actionId`,`textField_label`,`textField_value`,`textField_placeHolder`,`checkBox_actionId`,`checkBox_label`,`checkBox_value`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DashboardElementEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sectionId)
        statement.bindLong(3, entity.elementOrder.toLong())
        val _tmpButton: DashboardElementButton? = entity.button
        if (_tmpButton != null) {
          statement.bindText(4, _tmpButton.text)
          statement.bindText(5, _tmpButton.actionId)
        } else {
          statement.bindNull(4)
          statement.bindNull(5)
        }
        val _tmpText: DashboardElementText? = entity.text
        if (_tmpText != null) {
          statement.bindText(6, _tmpText.label)
          statement.bindText(7, _tmpText.value)
          val _tmpColor: Int? = _tmpText.color
          if (_tmpColor == null) {
            statement.bindNull(8)
          } else {
            statement.bindLong(8, _tmpColor.toLong())
          }
        } else {
          statement.bindNull(6)
          statement.bindNull(7)
          statement.bindNull(8)
        }
        val _tmpPlainText: DashboardElementPlainText? = entity.plainText
        if (_tmpPlainText != null) {
          statement.bindText(9, _tmpPlainText.label)
          statement.bindText(10, _tmpPlainText.value)
          statement.bindText(11, _tmpPlainText.type)
        } else {
          statement.bindNull(9)
          statement.bindNull(10)
          statement.bindNull(11)
        }
        val _tmpTextField: DashboardElementTextField? = entity.textField
        if (_tmpTextField != null) {
          statement.bindText(12, _tmpTextField.actionId)
          statement.bindText(13, _tmpTextField.label)
          statement.bindText(14, _tmpTextField.value)
          val _tmpPlaceHolder: String? = _tmpTextField.placeHolder
          if (_tmpPlaceHolder == null) {
            statement.bindNull(15)
          } else {
            statement.bindText(15, _tmpPlaceHolder)
          }
        } else {
          statement.bindNull(12)
          statement.bindNull(13)
          statement.bindNull(14)
          statement.bindNull(15)
        }
        val _tmpCheckBox: DashboardElementCheckBox? = entity.checkBox
        if (_tmpCheckBox != null) {
          statement.bindText(16, _tmpCheckBox.actionId)
          statement.bindText(17, _tmpCheckBox.label)
          val _tmp: Int = if (_tmpCheckBox.value) 1 else 0
          statement.bindLong(18, _tmp.toLong())
        } else {
          statement.bindNull(16)
          statement.bindNull(17)
          statement.bindNull(18)
        }
      }
    }
  }

  public override suspend fun insertDashboard(dashboard: DashboardEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfDashboardEntity.insertAndReturnId(_connection, dashboard)
    _result
  }

  public override suspend fun insertSections(sections: List<DashboardSectionEntity>): List<Long> =
      performSuspending(__db, false, true) { _connection ->
    val _result: List<Long> =
        __insertAdapterOfDashboardSectionEntity.insertAndReturnIdsList(_connection, sections)
    _result
  }

  public override suspend fun insertDashboardElements(elements: List<DashboardElementEntity>): Unit
      = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDashboardElementEntity.insert(_connection, elements)
  }

  public override suspend fun saveFullDashboard(deviceId: String, dashboard: DashboardDomainModel):
      Unit = performInTransactionSuspending(__db) {
    super@FloconDashboardDao_Impl.saveFullDashboard(deviceId, dashboard)
  }

  public override fun observeDashboardWithSectionsAndElements(deviceId: String,
      dashboardId: String): Flow<DashboardWithSectionsAndElements?> {
    val _sql: String = """
        |
        |        SELECT * FROM DashboardEntity 
        |        WHERE deviceId = ? AND dashboardId = ?
        |        LIMIT 1
        |    
        """.trimMargin()
    return createFlow(__db, true, arrayOf("DashboardElementEntity", "DashboardSectionEntity",
        "DashboardEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, dashboardId)
        val _columnIndexOfDashboardId: Int = getColumnIndexOrThrow(_stmt, "dashboardId")
        val _columnIndexOfDeviceId: Int = getColumnIndexOrThrow(_stmt, "deviceId")
        val _collectionSectionsWithElements: MutableMap<String, MutableList<SectionWithElements>> =
            mutableMapOf()
        while (_stmt.step()) {
          val _tmpKey: String
          _tmpKey = _stmt.getText(_columnIndexOfDashboardId)
          if (!_collectionSectionsWithElements.containsKey(_tmpKey)) {
            _collectionSectionsWithElements.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipDashboardSectionEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomSectionWithElements(_connection,
            _collectionSectionsWithElements)
        val _result: DashboardWithSectionsAndElements?
        if (_stmt.step()) {
          val _tmpDashboard: DashboardEntity
          val _tmpDashboardId: String
          _tmpDashboardId = _stmt.getText(_columnIndexOfDashboardId)
          val _tmpDeviceId: String
          _tmpDeviceId = _stmt.getText(_columnIndexOfDeviceId)
          _tmpDashboard = DashboardEntity(_tmpDashboardId,_tmpDeviceId)
          val _tmpSectionsWithElementsCollection: MutableList<SectionWithElements>
          val _tmpKey_1: String
          _tmpKey_1 = _stmt.getText(_columnIndexOfDashboardId)
          _tmpSectionsWithElementsCollection = _collectionSectionsWithElements.getValue(_tmpKey_1)
          _result =
              DashboardWithSectionsAndElements(_tmpDashboard,_tmpSectionsWithElementsCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeDeviceDashboards(deviceId: String): Flow<List<String>> {
    val _sql: String = """
        |
        |        SELECT dashboardId
        |        FROM DashboardEntity 
        |        WHERE deviceId = ? 
        |    
        """.trimMargin()
    return createFlow(__db, true, arrayOf("DashboardEntity")) { _connection ->
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

  public override suspend fun clearDashboardByDeviceAndId(deviceId: String, dashboard: String) {
    val _sql: String = """
        |
        |        DELETE FROM DashboardEntity 
        |        WHERE deviceId = ? AND dashboardId = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deviceId)
        _argIndex = 2
        _stmt.bindText(_argIndex, dashboard)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private
      fun __fetchRelationshipDashboardElementEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomModelDashboardElementEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<DashboardElementEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipDashboardElementEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomModelDashboardElementEntity(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`sectionId`,`elementOrder`,`button_text`,`button_actionId`,`text_label`,`text_value`,`text_color`,`plainText_label`,`plainText_value`,`plainText_type`,`textField_actionId`,`textField_label`,`textField_value`,`textField_placeHolder`,`checkBox_actionId`,`checkBox_label`,`checkBox_value` FROM `DashboardElementEntity` WHERE `sectionId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "sectionId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfSectionId: Int = 1
      val _columnIndexOfElementOrder: Int = 2
      val _columnIndexOfText: Int = 3
      val _columnIndexOfActionId: Int = 4
      val _columnIndexOfLabel: Int = 5
      val _columnIndexOfValue: Int = 6
      val _columnIndexOfColor: Int = 7
      val _columnIndexOfLabel_1: Int = 8
      val _columnIndexOfValue_1: Int = 9
      val _columnIndexOfType: Int = 10
      val _columnIndexOfActionId_1: Int = 11
      val _columnIndexOfLabel_2: Int = 12
      val _columnIndexOfValue_2: Int = 13
      val _columnIndexOfPlaceHolder: Int = 14
      val _columnIndexOfActionId_2: Int = 15
      val _columnIndexOfLabel_3: Int = 16
      val _columnIndexOfValue_3: Int = 17
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<DashboardElementEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: DashboardElementEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSectionId: Long
          _tmpSectionId = _stmt.getLong(_columnIndexOfSectionId)
          val _tmpElementOrder: Int
          _tmpElementOrder = _stmt.getLong(_columnIndexOfElementOrder).toInt()
          val _tmpButton: DashboardElementButton?
          if (!(_stmt.isNull(_columnIndexOfText) && _stmt.isNull(_columnIndexOfActionId))) {
            val _tmpText_1: String
            _tmpText_1 = _stmt.getText(_columnIndexOfText)
            val _tmpActionId: String
            _tmpActionId = _stmt.getText(_columnIndexOfActionId)
            _tmpButton = DashboardElementButton(_tmpText_1,_tmpActionId)
          } else {
            _tmpButton = null
          }
          val _tmpText: DashboardElementText?
          if (!(_stmt.isNull(_columnIndexOfLabel) && _stmt.isNull(_columnIndexOfValue) &&
              _stmt.isNull(_columnIndexOfColor))) {
            val _tmpLabel: String
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
            val _tmpValue: String
            _tmpValue = _stmt.getText(_columnIndexOfValue)
            val _tmpColor: Int?
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null
            } else {
              _tmpColor = _stmt.getLong(_columnIndexOfColor).toInt()
            }
            _tmpText = DashboardElementText(_tmpLabel,_tmpValue,_tmpColor)
          } else {
            _tmpText = null
          }
          val _tmpPlainText: DashboardElementPlainText?
          if (!(_stmt.isNull(_columnIndexOfLabel_1) && _stmt.isNull(_columnIndexOfValue_1) &&
              _stmt.isNull(_columnIndexOfType))) {
            val _tmpLabel_1: String
            _tmpLabel_1 = _stmt.getText(_columnIndexOfLabel_1)
            val _tmpValue_1: String
            _tmpValue_1 = _stmt.getText(_columnIndexOfValue_1)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            _tmpPlainText = DashboardElementPlainText(_tmpLabel_1,_tmpValue_1,_tmpType)
          } else {
            _tmpPlainText = null
          }
          val _tmpTextField: DashboardElementTextField?
          if (!(_stmt.isNull(_columnIndexOfActionId_1) && _stmt.isNull(_columnIndexOfLabel_2) &&
              _stmt.isNull(_columnIndexOfValue_2) && _stmt.isNull(_columnIndexOfPlaceHolder))) {
            val _tmpActionId_1: String
            _tmpActionId_1 = _stmt.getText(_columnIndexOfActionId_1)
            val _tmpLabel_2: String
            _tmpLabel_2 = _stmt.getText(_columnIndexOfLabel_2)
            val _tmpValue_2: String
            _tmpValue_2 = _stmt.getText(_columnIndexOfValue_2)
            val _tmpPlaceHolder: String?
            if (_stmt.isNull(_columnIndexOfPlaceHolder)) {
              _tmpPlaceHolder = null
            } else {
              _tmpPlaceHolder = _stmt.getText(_columnIndexOfPlaceHolder)
            }
            _tmpTextField =
                DashboardElementTextField(_tmpActionId_1,_tmpLabel_2,_tmpValue_2,_tmpPlaceHolder)
          } else {
            _tmpTextField = null
          }
          val _tmpCheckBox: DashboardElementCheckBox?
          if (!(_stmt.isNull(_columnIndexOfActionId_2) && _stmt.isNull(_columnIndexOfLabel_3) &&
              _stmt.isNull(_columnIndexOfValue_3))) {
            val _tmpActionId_2: String
            _tmpActionId_2 = _stmt.getText(_columnIndexOfActionId_2)
            val _tmpLabel_3: String
            _tmpLabel_3 = _stmt.getText(_columnIndexOfLabel_3)
            val _tmpValue_3: Boolean
            val _tmp: Int
            _tmp = _stmt.getLong(_columnIndexOfValue_3).toInt()
            _tmpValue_3 = _tmp != 0
            _tmpCheckBox = DashboardElementCheckBox(_tmpActionId_2,_tmpLabel_3,_tmpValue_3)
          } else {
            _tmpCheckBox = null
          }
          _item_1 =
              DashboardElementEntity(_tmpId,_tmpSectionId,_tmpElementOrder,_tmpButton,_tmpText,_tmpPlainText,_tmpTextField,_tmpCheckBox)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshipDashboardSectionEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomSectionWithElements(_connection: SQLiteConnection,
      _map: MutableMap<String, MutableList<SectionWithElements>>) {
    val __mapKeySet: Set<String> = _map.keys
    if (__mapKeySet.isEmpty()) {
      return
    }
    if (_map.size > 999) {
      recursiveFetchMap(_map, true) { _tmpMap ->
        __fetchRelationshipDashboardSectionEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomSectionWithElements(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`dashboardId`,`sectionOrder`,`name` FROM `DashboardSectionEntity` WHERE `dashboardId` IN (")
    val _inputSize: Int = __mapKeySet.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (_item: String in __mapKeySet) {
      _stmt.bindText(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "dashboardId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfDashboardId: Int = 1
      val _columnIndexOfSectionOrder: Int = 2
      val _columnIndexOfName: Int = 3
      val _collectionElements: LongSparseArray<MutableList<DashboardElementEntity>> =
          LongSparseArray<MutableList<DashboardElementEntity>>()
      while (_stmt.step()) {
        val _tmpKey: Long?
        if (_stmt.isNull(_columnIndexOfId)) {
          _tmpKey = null
        } else {
          _tmpKey = _stmt.getLong(_columnIndexOfId)
        }
        if (_tmpKey != null) {
          if (!_collectionElements.containsKey(_tmpKey)) {
            _collectionElements.put(_tmpKey, mutableListOf())
          }
        }
      }
      _stmt.reset()
      __fetchRelationshipDashboardElementEntityAsioGithubOpenfloconFlocondesktopFeaturesDashboardDataDatasourcesRoomModelDashboardElementEntity(_connection,
          _collectionElements)
      while (_stmt.step()) {
        val _tmpKey_1: String
        _tmpKey_1 = _stmt.getText(_itemKeyIndex)
        val _tmpRelation: MutableList<SectionWithElements>? = _map.get(_tmpKey_1)
        if (_tmpRelation != null) {
          val _item_1: SectionWithElements
          val _tmpSection: DashboardSectionEntity?
          if (!(_stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfDashboardId) &&
              _stmt.isNull(_columnIndexOfSectionOrder) && _stmt.isNull(_columnIndexOfName))) {
            val _tmpId: Long
            _tmpId = _stmt.getLong(_columnIndexOfId)
            val _tmpDashboardId: String
            _tmpDashboardId = _stmt.getText(_columnIndexOfDashboardId)
            val _tmpSectionOrder: Int
            _tmpSectionOrder = _stmt.getLong(_columnIndexOfSectionOrder).toInt()
            val _tmpName: String
            _tmpName = _stmt.getText(_columnIndexOfName)
            _tmpSection = DashboardSectionEntity(_tmpId,_tmpDashboardId,_tmpSectionOrder,_tmpName)
          } else {
            _tmpSection = null
          }
          val _tmpElementsCollection: MutableList<DashboardElementEntity>
          val _tmpKey_2: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpKey_2 = null
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          }
          if (_tmpKey_2 != null) {
            _tmpElementsCollection = checkNotNull(_collectionElements.get(_tmpKey_2))
          } else {
            _tmpElementsCollection = mutableListOf()
          }
          _item_1 = SectionWithElements(_tmpSection,_tmpElementsCollection)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
