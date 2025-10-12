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


        val longBreeds = listOf(
            "Golden Retriever royal de la lignée légendaire des chiens des montagnes dorées du nord-ouest canadien, " +
                    "descendant direct des plus nobles compagnons de chasse de l’ère victorienne, connu pour sa loyauté infinie, " +
                    "sa douceur de caractère et sa passion inébranlable pour les flaques d’eau et les bâtons jetés dans les rivières.",

            "Basset des plaines méridionales au flair infaillible, issu d’une dynastie ancienne de chiens détectives spécialisés " +
                    "dans la recherche de friandises disparues, capable de renifler la moindre trace de biscuit à plusieurs kilomètres, " +
                    "tout en gardant un regard plein de mélancolie et de sagesse millénaire.",

            "Husky sibérien au pelage argenté, né pour courir dans les neiges éternelles et hurler à la lune les chants oubliés " +
                    "des aurores boréales. Sa résistance légendaire et son regard bleu perçant en font un symbole de liberté, " +
                    "d’endurance et d’amitié indestructible entre l’homme et le chien.",

            "Berger australien des terres rouges, gardien des troupeaux et des rêves, au regard vif et au cœur débordant d’énergie. " +
                    "Il danse entre les collines et les vents, tissant des liens invisibles entre la nature et l’esprit des vivants, " +
                    "incarnation du courage et de la curiosité sans limites."
        )

        for (i in 1..20) {
            val randomBreed = longBreeds.random()

            dogDatabase.dogDao().insertDog(
                DogEntity(
                    id = 10L + i,
                    name = "dog$i",
                    breed = randomBreed,
                    age = (1..15).random()
                )
            )
        }

        foodDatabase.foodDao().insertFood(
            FoodEntity(
                id = 1, name = "Cheese Burger", type = "Sandwitch", calories = 1234,
            )
        )
    }
}