package io.github.openflocon.flocon.myapplication.multi.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanWithDogEntity
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
public class DogDao_Impl(
  __db: RoomDatabase,
) : DogDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfDogEntity: EntityUpsertAdapter<DogEntity>

  private val __upsertAdapterOfHumanEntity: EntityUpsertAdapter<HumanEntity>

  private val __upsertAdapterOfHumanWithDogEntity: EntityUpsertAdapter<HumanWithDogEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfDogEntity = EntityUpsertAdapter<DogEntity>(object :
        EntityInsertAdapter<DogEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `DogEntity` (`id`,`name`,`breed`,`pictureUrl`,`age`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.breed)
        statement.bindText(4, entity.pictureUrl)
        statement.bindLong(5, entity.age.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<DogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `DogEntity` SET `id` = ?,`name` = ?,`breed` = ?,`pictureUrl` = ?,`age` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.breed)
        statement.bindText(4, entity.pictureUrl)
        statement.bindLong(5, entity.age.toLong())
        statement.bindLong(6, entity.id)
      }
    })
    this.__upsertAdapterOfHumanEntity = EntityUpsertAdapter<HumanEntity>(object :
        EntityInsertAdapter<HumanEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `HumanEntity` (`id`,`firstName`,`name`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HumanEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.firstName)
        statement.bindText(3, entity.name)
      }
    }, object : EntityDeleteOrUpdateAdapter<HumanEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `HumanEntity` SET `id` = ?,`firstName` = ?,`name` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HumanEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.firstName)
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.id)
      }
    })
    this.__upsertAdapterOfHumanWithDogEntity = EntityUpsertAdapter<HumanWithDogEntity>(object :
        EntityInsertAdapter<HumanWithDogEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `HumanWithDogEntity` (`humanId`,`dogId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HumanWithDogEntity) {
        statement.bindLong(1, entity.humanId)
        statement.bindLong(2, entity.dogId)
      }
    }, object : EntityDeleteOrUpdateAdapter<HumanWithDogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `HumanWithDogEntity` SET `humanId` = ?,`dogId` = ? WHERE `humanId` = ? AND `dogId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HumanWithDogEntity) {
        statement.bindLong(1, entity.humanId)
        statement.bindLong(2, entity.dogId)
        statement.bindLong(3, entity.humanId)
        statement.bindLong(4, entity.dogId)
      }
    })
  }

  public override suspend fun insertDog(dog: DogEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfDogEntity.upsert(_connection, dog)
  }

  public override suspend fun insertHuman(human: HumanEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfHumanEntity.upsert(_connection, human)
  }

  public override suspend fun insertHumanWithDogEntity(humanWithDog: HumanWithDogEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfHumanWithDogEntity.upsert(_connection, humanWithDog)
  }

  public override fun getAllDogs(): Flow<List<DogEntity>> {
    val _sql: String = "SELECT * FROM DogEntity"
    return createFlow(__db, false, arrayOf("DogEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfPictureUrl: Int = getColumnIndexOrThrow(_stmt, "pictureUrl")
        val _columnIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _result: MutableList<DogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpPictureUrl: String
          _tmpPictureUrl = _stmt.getText(_columnIndexOfPictureUrl)
          val _tmpAge: Int
          _tmpAge = _stmt.getLong(_columnIndexOfAge).toInt()
          _item = DogEntity(_tmpId,_tmpName,_tmpBreed,_tmpPictureUrl,_tmpAge)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
