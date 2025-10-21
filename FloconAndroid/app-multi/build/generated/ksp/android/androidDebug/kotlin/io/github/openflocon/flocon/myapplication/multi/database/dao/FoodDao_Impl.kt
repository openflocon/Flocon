package io.github.openflocon.flocon.myapplication.multi.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocon.myapplication.multi.database.model.FoodEntity
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
public class FoodDao_Impl(
  __db: RoomDatabase,
) : FoodDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFoodEntity: EntityInsertAdapter<FoodEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFoodEntity = object : EntityInsertAdapter<FoodEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `FoodEntity` (`id`,`name`,`type`,`calories`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FoodEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.calories.toLong())
      }
    }
  }

  public override suspend fun insertFood(food: FoodEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFoodEntity.insert(_connection, food)
  }

  public override fun getAllFoods(): Flow<List<FoodEntity>> {
    val _sql: String = "SELECT * FROM FoodEntity"
    return createFlow(__db, false, arrayOf("FoodEntity")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCalories: Int = getColumnIndexOrThrow(_stmt, "calories")
        val _result: MutableList<FoodEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FoodEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCalories: Int
          _tmpCalories = _stmt.getLong(_columnIndexOfCalories).toInt()
          _item = FoodEntity(_tmpId,_tmpName,_tmpType,_tmpCalories)
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
