package io.github.openflocon.flocon.myapplication.multi.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.openflocon.flocon.myapplication.multi.database.dao.DogDao
import io.github.openflocon.flocon.myapplication.multi.database.dao.DogDao_Impl
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
public class DogDatabase_Impl : DogDatabase() {
  private val _dogDao: Lazy<DogDao> = lazy {
    DogDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(3,
        "8d3426e57ab8dbb560a22937e6c6b60e", "8fa53ad630d08dd2dd3be000cb33e0b8") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `DogEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `breed` TEXT NOT NULL, `pictureUrl` TEXT NOT NULL, `age` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `HumanEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firstName` TEXT NOT NULL, `name` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `HumanWithDogEntity` (`humanId` INTEGER NOT NULL, `dogId` INTEGER NOT NULL, PRIMARY KEY(`humanId`, `dogId`), FOREIGN KEY(`humanId`) REFERENCES `HumanEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`dogId`) REFERENCES `DogEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8d3426e57ab8dbb560a22937e6c6b60e')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `DogEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `HumanEntity`")
        connection.execSQL("DROP TABLE IF EXISTS `HumanWithDogEntity`")
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
        val _columnsDogEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDogEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDogEntity.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDogEntity.put("breed", TableInfo.Column("breed", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDogEntity.put("pictureUrl", TableInfo.Column("pictureUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDogEntity.put("age", TableInfo.Column("age", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDogEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDogEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDogEntity: TableInfo = TableInfo("DogEntity", _columnsDogEntity,
            _foreignKeysDogEntity, _indicesDogEntity)
        val _existingDogEntity: TableInfo = read(connection, "DogEntity")
        if (!_infoDogEntity.equals(_existingDogEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |DogEntity(io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity).
              | Expected:
              |""".trimMargin() + _infoDogEntity + """
              |
              | Found:
              |""".trimMargin() + _existingDogEntity)
        }
        val _columnsHumanEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHumanEntity.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHumanEntity.put("firstName", TableInfo.Column("firstName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHumanEntity.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHumanEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHumanEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHumanEntity: TableInfo = TableInfo("HumanEntity", _columnsHumanEntity,
            _foreignKeysHumanEntity, _indicesHumanEntity)
        val _existingHumanEntity: TableInfo = read(connection, "HumanEntity")
        if (!_infoHumanEntity.equals(_existingHumanEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |HumanEntity(io.github.openflocon.flocon.myapplication.multi.database.model.HumanEntity).
              | Expected:
              |""".trimMargin() + _infoHumanEntity + """
              |
              | Found:
              |""".trimMargin() + _existingHumanEntity)
        }
        val _columnsHumanWithDogEntity: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHumanWithDogEntity.put("humanId", TableInfo.Column("humanId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHumanWithDogEntity.put("dogId", TableInfo.Column("dogId", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHumanWithDogEntity: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysHumanWithDogEntity.add(TableInfo.ForeignKey("HumanEntity", "CASCADE",
            "NO ACTION", listOf("humanId"), listOf("id")))
        _foreignKeysHumanWithDogEntity.add(TableInfo.ForeignKey("DogEntity", "CASCADE", "NO ACTION",
            listOf("dogId"), listOf("id")))
        val _indicesHumanWithDogEntity: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHumanWithDogEntity: TableInfo = TableInfo("HumanWithDogEntity",
            _columnsHumanWithDogEntity, _foreignKeysHumanWithDogEntity, _indicesHumanWithDogEntity)
        val _existingHumanWithDogEntity: TableInfo = read(connection, "HumanWithDogEntity")
        if (!_infoHumanWithDogEntity.equals(_existingHumanWithDogEntity)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |HumanWithDogEntity(io.github.openflocon.flocon.myapplication.multi.database.model.HumanWithDogEntity).
              | Expected:
              |""".trimMargin() + _infoHumanWithDogEntity + """
              |
              | Found:
              |""".trimMargin() + _existingHumanWithDogEntity)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "DogEntity", "HumanEntity",
        "HumanWithDogEntity")
  }

  public override fun clearAllTables() {
    super.performClear(true, "DogEntity", "HumanEntity", "HumanWithDogEntity")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(DogDao::class, DogDao_Impl.getRequiredConverters())
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

  public override fun dogDao(): DogDao = _dogDao.value
}
