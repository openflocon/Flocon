package io.github.openflocon.flocon.myapplication.database

import android.content.Context
import io.github.openflocon.flocon.myapplication.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.database.model.FoodEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun initializeDatabases(context: Context) {
    val dogDatabase = DogDatabase.getDatabase(context)
    val foodDatabase = FoodDatabase.getDatabase(context)
    GlobalScope.launch {
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 1, name = "Flocon", breed = "Golden Retriever", age = 6,
            )
        )
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 2, name = "Vanille", breed = "Basset", age = 2,
            )
        )
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 3, name = "Scoubi", breed = "Yorkshire", age = 2,
            )
        )
        foodDatabase.foodDao().insertFood(
            FoodEntity(
                id = 1, name = "Cheese Burger", type = "Sandwitch", calories = 1234,
            )
        )
    }
}