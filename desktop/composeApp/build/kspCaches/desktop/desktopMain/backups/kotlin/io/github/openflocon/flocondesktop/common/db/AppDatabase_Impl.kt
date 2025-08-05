package io.github.openflocon.flocondesktop.common.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.openflocon.flocondesktop.features.analytics.`data`.datasource.local.FloconAnalyticsDao
import io.github.openflocon.flocondesktop.features.analytics.`data`.datasource.local.FloconAnalyticsDao_Impl
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.FloconDashboardDao
import io.github.openflocon.flocondesktop.features.dashboard.`data`.datasources.room.FloconDashboardDao_Impl
import io.github.openflocon.flocondesktop.features.database.`data`.datasource.local.QueryDao
import io.github.openflocon.flocondesktop.features.database.`data`.datasource.local.QueryDao_Impl
import io.github.openflocon.flocondesktop.features.deeplinks.`data`.datasource.room.FloconDeeplinkDao
import io.github.openflocon.flocondesktop.features.deeplinks.`data`.datasource.room.FloconDeeplinkDao_Impl
import io.github.openflocon.flocondesktop.features.files.`data`.datasources.FloconFileDao
import io.github.openflocon.flocondesktop.features.files.`data`.datasources.FloconFileDao_Impl
import io.github.openflocon.flocondesktop.features.images.`data`.datasources.local.FloconImageDao
import io.github.openflocon.flocondesktop.features.images.`data`.datasources.local.FloconImageDao_Impl
import io.github.openflocon.flocondesktop.features.network.`data`.datasource.local.FloconHttpRequestDao
import io.github.openflocon.flocondesktop.features.network.`data`.datasource.local.FloconHttpRequestDao_Impl
import io.github.openflocon.flocondesktop.features.table.`data`.datasource.local.FloconTableDao
import io.github.openflocon.flocondesktop.features.table.`data`.datasource.local.FloconTableDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _floconHttpRequestDao: Lazy<FloconHttpRequestDao> = lazy {
    FloconHttpRequestDao_Impl(this)
  }

  public override val httpRequestDao: FloconHttpRequestDao
    get() = _floconHttpRequestDao.value

  private val _floconFileDao: Lazy<FloconFileDao> = lazy {
    FloconFileDao_Impl(this)
  }

  public override val fileDao: FloconFileDao
    get() = _floconFileDao.value

  private val _floconDashboardDao: Lazy<FloconDashboardDao> = lazy {
    FloconDashboardDao_Impl(this)
  }

  public override val dashboardDao: FloconDashboardDao
    get() = _floconDashboardDao.value

  private val _floconTableDao: Lazy<FloconTableDao> = lazy {
    FloconTableDao_Impl(this)
  }

  public override val tableDao: FloconTableDao
    get() = _floconTableDao.value

  private val _floconImageDao: Lazy<FloconImageDao> = lazy {
    FloconImageDao_Impl(this)
  }

  public override val imageDao: FloconImageDao
    get() = _floconImageDao.value

  private val _queryDao: Lazy<QueryDao> = lazy {
    QueryDao_Impl(this)
  }

  public override val queryDao: QueryDao
    get() = _queryDao.value

  private val _floconDeeplinkDao: Lazy<FloconDeeplinkDao> = lazy {
    FloconDeeplinkDao_Impl(this)
  }

  public override val deeplinkDao: FloconDeeplinkDao
    get() = _floconDeeplinkDao.value

  private val _floconAnalyticsDao: Lazy<FloconAnalyticsDao> = lazy {
    FloconAnalyticsDao_Impl(this)
  }

  public override val analyticsDao: FloconAnalyticsDao
    get() = _floconAnalyticsDao.value

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(27,
        "6602c9e782a66f200b014ac2cfb4ef99", "250c0bcd44b7b08ea5ef2b860633d4fe") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `FloconHttpRequestEntity` (`uuid` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `url` TEXT NOT NULL, `method` TEXT NOT NULL, `startTime` INTEGER NOT NULL, `durationMs` REAL NOT NULL, `requestHeaders` TEXT NOT NULL, `requestBody` TEXT, `requestByteSize` INTEGER NOT NULL, `responseContentType` TEXT, `responseBody` TEXT, `responseHeaders` TEXT NOT NULL, `responseByteSize` INTEGER NOT NULL, `graphql_query` TEXT, `graphql_operationType` TEXT, `graphql_isSuccess` INTEGER, `graphql_responseHttpCode` INTEGER, `http_responseHttpCode` INTEGER, `grpc_responseStatus` TEXT, PRIMARY KEY(`uuid`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `FileEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceId` TEXT NOT NULL, `name` TEXT NOT NULL, `isDirectory` INTEGER NOT NULL, `path` TEXT NOT NULL, `parentPath` TEXT NOT NULL, `size` INTEGER NOT NULL, `lastModifiedTimestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DashboardEntity` (`dashboardId` TEXT NOT NULL, `deviceId` TEXT NOT NULL, PRIMARY KEY(`dashboardId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_DashboardEntity_dashboardId` ON `DashboardEntity` (`dashboardId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_DashboardEntity_deviceId` ON `DashboardEntity` (`deviceId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DashboardSectionEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dashboardId` TEXT NOT NULL, `sectionOrder` INTEGER NOT NULL, `name` TEXT NOT NULL, FOREIGN KEY(`dashboardId`) REFERENCES `DashboardEntity`(`dashboardId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_DashboardSectionEntity_dashboardId` ON `DashboardSectionEntity` (`dashboardId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_DashboardSectionEntity_dashboardId_sectionOrder` ON `DashboardSectionEntity` (`dashboardId`, `sectionOrder`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DashboardElementEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sectionId` INTEGER NOT NULL, `elementOrder` INTEGER NOT NULL, `button_text` TEXT, `button_actionId` TEXT, `text_label` TEXT, `text_value` TEXT, `text_color` INTEGER, `plainText_label` TEXT, `plainText_value` TEXT, `plainText_type` TEXT, `textField_actionId` TEXT, `textField_label` TEXT, `textField_value` TEXT, `textField_placeHolder` TEXT, `checkBox_actionId` TEXT, `checkBox_label` TEXT, `checkBox_value` INTEGER, FOREIGN KEY(`sectionId`) REFERENCES `DashboardSectionEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_DashboardElementEntity_sectionId` ON `DashboardElementEntity` (`sectionId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_DashboardElementEntity_sectionId_elementOrder` ON `DashboardElementEntity` (`sectionId`, `elementOrder`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `TableEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceId` TEXT NOT NULL, `name` TEXT NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_TableEntity_deviceId_name` ON `TableEntity` (`deviceId`, `name`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `TableItemEntity` (`itemId` TEXT NOT NULL, `tableId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `columnsNames` TEXT NOT NULL, `values` TEXT NOT NULL, PRIMARY KEY(`itemId`), FOREIGN KEY(`tableId`) REFERENCES `TableEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_TableItemEntity_tableId` ON `TableItemEntity` (`tableId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DeviceImageEntity` (`deviceId` TEXT NOT NULL, `url` TEXT NOT NULL, `time` INTEGER NOT NULL, PRIMARY KEY(`deviceId`, `url`, `time`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `SuccessQueryEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceId` TEXT NOT NULL, `databaseId` TEXT NOT NULL, `queryString` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_SuccessQueryEntity_deviceId_databaseId_queryString` ON `SuccessQueryEntity` (`deviceId`, `databaseId`, `queryString`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_SuccessQueryEntity_databaseId` ON `SuccessQueryEntity` (`databaseId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DeeplinkEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceId` TEXT NOT NULL, `link` TEXT NOT NULL, `label` TEXT, `description` TEXT)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_DeeplinkEntity_deviceId` ON `DeeplinkEntity` (`deviceId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_DeeplinkEntity_deviceId_link` ON `DeeplinkEntity` (`deviceId`, `link`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `AnalyticsItemEntity` (`itemId` TEXT NOT NULL, `analyticsTableId` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `eventName` TEXT NOT NULL, `propertiesColumnsNames` TEXT NOT NULL, `propertiesValues` TEXT NOT NULL, PRIMARY KEY(`itemId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_AnalyticsItemEntity_deviceId_analyticsTableId` ON `AnalyticsItemEntity` (`deviceId`, `analyticsTableId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6602c9e782a66f200b014ac2cfb4ef99')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `FloconHttpRequestEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `FileEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `DashboardEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `DashboardSectionEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `DashboardElementEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `TableEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `TableItemEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `DeviceImageEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `SuccessQueryEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `DeeplinkEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `AnalyticsItemEntity`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsFloconHttpRequestEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFloconHttpRequestEntity.put("uuid", TableInfo.Column("uuid", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("url", TableInfo.Column("url", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("method", TableInfo.Column("method", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("startTime", TableInfo.Column("startTime", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("durationMs", TableInfo.Column("durationMs", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("requestHeaders", TableInfo.Column("requestHeaders",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("requestBody", TableInfo.Column("requestBody", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("requestByteSize", TableInfo.Column("requestByteSize",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("responseContentType",
            TableInfo.Column("responseContentType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("responseBody", TableInfo.Column("responseBody", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("responseHeaders", TableInfo.Column("responseHeaders",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("responseByteSize", TableInfo.Column("responseByteSize",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("graphql_query", TableInfo.Column("graphql_query",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("graphql_operationType",
            TableInfo.Column("graphql_operationType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("graphql_isSuccess",
            TableInfo.Column("graphql_isSuccess", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("graphql_responseHttpCode",
            TableInfo.Column("graphql_responseHttpCode", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("http_responseHttpCode",
            TableInfo.Column("http_responseHttpCode", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFloconHttpRequestEntity.put("grpc_responseStatus",
            TableInfo.Column("grpc_responseStatus", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFloconHttpRequestEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFloconHttpRequestEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFloconHttpRequestEntity: TableInfo = TableInfo("FloconHttpRequestEntity",
            _columnsFloconHttpRequestEntity, _foreignKeysFloconHttpRequestEntity,
            _indicesFloconHttpRequestEntity)
        val _existingFloconHttpRequestEntity: TableInfo = read(connection,
            "FloconHttpRequestEntity")
        if (!_infoFloconHttpRequestEntity.equals(_existingFloconHttpRequestEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |FloconHttpRequestEntity(io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntity).
              | Expected:
              |""".trimMargin() + _infoFloconHttpRequestEntity + """
              |
              | Found:
              |""".trimMargin() + _existingFloconHttpRequestEntity)
        }
        val _columnsFileEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFileEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("isDirectory", TableInfo.Column("isDirectory", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("path", TableInfo.Column("path", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("parentPath", TableInfo.Column("parentPath", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("size", TableInfo.Column("size", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFileEntity.put("lastModifiedTimestamp", TableInfo.Column("lastModifiedTimestamp",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFileEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFileEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFileEntity: TableInfo = TableInfo("FileEntity", _columnsFileEntity,
            _foreignKeysFileEntity, _indicesFileEntity)
        val _existingFileEntity: TableInfo = read(connection, "FileEntity")
        if (!_infoFileEntity.equals(_existingFileEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |FileEntity(io.github.openflocon.flocondesktop.features.files.data.datasources.model.FileEntity).
              | Expected:
              |""".trimMargin() + _infoFileEntity + """
              |
              | Found:
              |""".trimMargin() + _existingFileEntity)
        }
        val _columnsDashboardEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDashboardEntity.put("dashboardId", TableInfo.Column("dashboardId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDashboardEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDashboardEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDashboardEntity.add(TableInfo.Index("index_DashboardEntity_dashboardId", false,
            listOf("dashboardId"), listOf("ASC")))
        _indicesDashboardEntity.add(TableInfo.Index("index_DashboardEntity_deviceId", false,
            listOf("deviceId"), listOf("ASC")))
        val _infoDashboardEntity: TableInfo = TableInfo("DashboardEntity", _columnsDashboardEntity,
            _foreignKeysDashboardEntity, _indicesDashboardEntity)
        val _existingDashboardEntity: TableInfo = read(connection, "DashboardEntity")
        if (!_infoDashboardEntity.equals(_existingDashboardEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DashboardEntity(io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity).
              | Expected:
              |""".trimMargin() + _infoDashboardEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDashboardEntity)
        }
        val _columnsDashboardSectionEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDashboardSectionEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardSectionEntity.put("dashboardId", TableInfo.Column("dashboardId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardSectionEntity.put("sectionOrder", TableInfo.Column("sectionOrder",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardSectionEntity.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDashboardSectionEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDashboardSectionEntity.add(TableInfo.ForeignKey("DashboardEntity", "CASCADE",
            "NO ACTION", listOf("dashboardId"), listOf("dashboardId")))
        val _indicesDashboardSectionEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDashboardSectionEntity.add(TableInfo.Index("index_DashboardSectionEntity_dashboardId",
            false, listOf("dashboardId"), listOf("ASC")))
        _indicesDashboardSectionEntity.add(TableInfo.Index("index_DashboardSectionEntity_dashboardId_sectionOrder",
            true, listOf("dashboardId", "sectionOrder"), listOf("ASC", "ASC")))
        val _infoDashboardSectionEntity: TableInfo = TableInfo("DashboardSectionEntity",
            _columnsDashboardSectionEntity, _foreignKeysDashboardSectionEntity,
            _indicesDashboardSectionEntity)
        val _existingDashboardSectionEntity: TableInfo = read(connection, "DashboardSectionEntity")
        if (!_infoDashboardSectionEntity.equals(_existingDashboardSectionEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DashboardSectionEntity(io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity).
              | Expected:
              |""".trimMargin() + _infoDashboardSectionEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDashboardSectionEntity)
        }
        val _columnsDashboardElementEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDashboardElementEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("sectionId", TableInfo.Column("sectionId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("elementOrder", TableInfo.Column("elementOrder",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("button_text", TableInfo.Column("button_text", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("button_actionId", TableInfo.Column("button_actionId",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("text_label", TableInfo.Column("text_label", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("text_value", TableInfo.Column("text_value", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("text_color", TableInfo.Column("text_color", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("plainText_label", TableInfo.Column("plainText_label",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("plainText_value", TableInfo.Column("plainText_value",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("plainText_type", TableInfo.Column("plainText_type",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("textField_actionId",
            TableInfo.Column("textField_actionId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("textField_label", TableInfo.Column("textField_label",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("textField_value", TableInfo.Column("textField_value",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("textField_placeHolder",
            TableInfo.Column("textField_placeHolder", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("checkBox_actionId",
            TableInfo.Column("checkBox_actionId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("checkBox_label", TableInfo.Column("checkBox_label",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardElementEntity.put("checkBox_value", TableInfo.Column("checkBox_value",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDashboardElementEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDashboardElementEntity.add(TableInfo.ForeignKey("DashboardSectionEntity",
            "CASCADE", "NO ACTION", listOf("sectionId"), listOf("id")))
        val _indicesDashboardElementEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDashboardElementEntity.add(TableInfo.Index("index_DashboardElementEntity_sectionId",
            false, listOf("sectionId"), listOf("ASC")))
        _indicesDashboardElementEntity.add(TableInfo.Index("index_DashboardElementEntity_sectionId_elementOrder",
            true, listOf("sectionId", "elementOrder"), listOf("ASC", "ASC")))
        val _infoDashboardElementEntity: TableInfo = TableInfo("DashboardElementEntity",
            _columnsDashboardElementEntity, _foreignKeysDashboardElementEntity,
            _indicesDashboardElementEntity)
        val _existingDashboardElementEntity: TableInfo = read(connection, "DashboardElementEntity")
        if (!_infoDashboardElementEntity.equals(_existingDashboardElementEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DashboardElementEntity(io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity).
              | Expected:
              |""".trimMargin() + _infoDashboardElementEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDashboardElementEntity)
        }
        val _columnsTableEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTableEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableEntity.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTableEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTableEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTableEntity.add(TableInfo.Index("index_TableEntity_deviceId_name", true,
            listOf("deviceId", "name"), listOf("ASC", "ASC")))
        val _infoTableEntity: TableInfo = TableInfo("TableEntity", _columnsTableEntity,
            _foreignKeysTableEntity, _indicesTableEntity)
        val _existingTableEntity: TableInfo = read(connection, "TableEntity")
        if (!_infoTableEntity.equals(_existingTableEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |TableEntity(io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity).
              | Expected:
              |""".trimMargin() + _infoTableEntity + """
              |
              | Found:
              |""".trimMargin() + _existingTableEntity)
        }
        val _columnsTableItemEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTableItemEntity.put("itemId", TableInfo.Column("itemId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableItemEntity.put("tableId", TableInfo.Column("tableId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTableItemEntity.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTableItemEntity.put("columnsNames", TableInfo.Column("columnsNames", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTableItemEntity.put("values", TableInfo.Column("values", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTableItemEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTableItemEntity.add(TableInfo.ForeignKey("TableEntity", "CASCADE", "NO ACTION",
            listOf("tableId"), listOf("id")))
        val _indicesTableItemEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTableItemEntity.add(TableInfo.Index("index_TableItemEntity_tableId", false,
            listOf("tableId"), listOf("ASC")))
        val _infoTableItemEntity: TableInfo = TableInfo("TableItemEntity", _columnsTableItemEntity,
            _foreignKeysTableItemEntity, _indicesTableItemEntity)
        val _existingTableItemEntity: TableInfo = read(connection, "TableItemEntity")
        if (!_infoTableItemEntity.equals(_existingTableItemEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |TableItemEntity(io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableItemEntity).
              | Expected:
              |""".trimMargin() + _infoTableItemEntity + """
              |
              | Found:
              |""".trimMargin() + _existingTableItemEntity)
        }
        val _columnsDeviceImageEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDeviceImageEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeviceImageEntity.put("url", TableInfo.Column("url", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeviceImageEntity.put("time", TableInfo.Column("time", "INTEGER", true, 3, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDeviceImageEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDeviceImageEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDeviceImageEntity: TableInfo = TableInfo("DeviceImageEntity",
            _columnsDeviceImageEntity, _foreignKeysDeviceImageEntity, _indicesDeviceImageEntity)
        val _existingDeviceImageEntity: TableInfo = read(connection, "DeviceImageEntity")
        if (!_infoDeviceImageEntity.equals(_existingDeviceImageEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DeviceImageEntity(io.github.openflocon.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity).
              | Expected:
              |""".trimMargin() + _infoDeviceImageEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDeviceImageEntity)
        }
        val _columnsSuccessQueryEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSuccessQueryEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSuccessQueryEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuccessQueryEntity.put("databaseId", TableInfo.Column("databaseId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuccessQueryEntity.put("queryString", TableInfo.Column("queryString", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuccessQueryEntity.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSuccessQueryEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSuccessQueryEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSuccessQueryEntity.add(TableInfo.Index("index_SuccessQueryEntity_deviceId_databaseId_queryString",
            true, listOf("deviceId", "databaseId", "queryString"), listOf("ASC", "ASC", "ASC")))
        _indicesSuccessQueryEntity.add(TableInfo.Index("index_SuccessQueryEntity_databaseId", false,
            listOf("databaseId"), listOf("ASC")))
        val _infoSuccessQueryEntity: TableInfo = TableInfo("SuccessQueryEntity",
            _columnsSuccessQueryEntity, _foreignKeysSuccessQueryEntity, _indicesSuccessQueryEntity)
        val _existingSuccessQueryEntity: TableInfo = read(connection, "SuccessQueryEntity")
        if (!_infoSuccessQueryEntity.equals(_existingSuccessQueryEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |SuccessQueryEntity(io.github.openflocon.flocondesktop.features.database.data.datasource.local.SuccessQueryEntity).
              | Expected:
              |""".trimMargin() + _infoSuccessQueryEntity + """
              |
              | Found:
              |""".trimMargin() + _existingSuccessQueryEntity)
        }
        val _columnsDeeplinkEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDeeplinkEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeeplinkEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeeplinkEntity.put("link", TableInfo.Column("link", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeeplinkEntity.put("label", TableInfo.Column("label", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeeplinkEntity.put("description", TableInfo.Column("description", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDeeplinkEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDeeplinkEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDeeplinkEntity.add(TableInfo.Index("index_DeeplinkEntity_deviceId", false,
            listOf("deviceId"), listOf("ASC")))
        _indicesDeeplinkEntity.add(TableInfo.Index("index_DeeplinkEntity_deviceId_link", true,
            listOf("deviceId", "link"), listOf("ASC", "ASC")))
        val _infoDeeplinkEntity: TableInfo = TableInfo("DeeplinkEntity", _columnsDeeplinkEntity,
            _foreignKeysDeeplinkEntity, _indicesDeeplinkEntity)
        val _existingDeeplinkEntity: TableInfo = read(connection, "DeeplinkEntity")
        if (!_infoDeeplinkEntity.equals(_existingDeeplinkEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DeeplinkEntity(io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity).
              | Expected:
              |""".trimMargin() + _infoDeeplinkEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDeeplinkEntity)
        }
        val _columnsAnalyticsItemEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAnalyticsItemEntity.put("itemId", TableInfo.Column("itemId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("analyticsTableId", TableInfo.Column("analyticsTableId",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("deviceId", TableInfo.Column("deviceId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("eventName", TableInfo.Column("eventName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("propertiesColumnsNames",
            TableInfo.Column("propertiesColumnsNames", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsItemEntity.put("propertiesValues", TableInfo.Column("propertiesValues",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAnalyticsItemEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAnalyticsItemEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAnalyticsItemEntity.add(TableInfo.Index("index_AnalyticsItemEntity_deviceId_analyticsTableId",
            false, listOf("deviceId", "analyticsTableId"), listOf("ASC", "ASC")))
        val _infoAnalyticsItemEntity: TableInfo = TableInfo("AnalyticsItemEntity",
            _columnsAnalyticsItemEntity, _foreignKeysAnalyticsItemEntity,
            _indicesAnalyticsItemEntity)
        val _existingAnalyticsItemEntity: TableInfo = read(connection, "AnalyticsItemEntity")
        if (!_infoAnalyticsItemEntity.equals(_existingAnalyticsItemEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |AnalyticsItemEntity(io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity).
              | Expected:
              |""".trimMargin() + _infoAnalyticsItemEntity + """
              |
              | Found:
              |""".trimMargin() + _existingAnalyticsItemEntity)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "FloconHttpRequestEntity",
        "FileEntity", "DashboardEntity", "DashboardSectionEntity", "DashboardElementEntity",
        "TableEntity", "TableItemEntity", "DeviceImageEntity", "SuccessQueryEntity",
        "DeeplinkEntity", "AnalyticsItemEntity")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(FloconHttpRequestDao::class,
        FloconHttpRequestDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconFileDao::class, FloconFileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconDashboardDao::class,
        FloconDashboardDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconTableDao::class, FloconTableDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconImageDao::class, FloconImageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(QueryDao::class, QueryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconDeeplinkDao::class, FloconDeeplinkDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FloconAnalyticsDao::class,
        FloconAnalyticsDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }
}
