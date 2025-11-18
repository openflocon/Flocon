package io.github.openflocon.flocon.myapplication.database

import android.content.Context
import androidx.room.Room
import io.github.openflocon.flocon.myapplication.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.database.model.FoodEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.database.model.HumanWithDogEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun initializeDatabases(context: Context) {
    val dogDatabase = DogDatabase.getDatabase(context)
    val foodDatabase = FoodDatabase.getDatabase(context)

    GlobalScope.launch {
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 1, name = "Flocon", breed = "Golden Retriever", age = 6,
                pictureUrl = "https://picsum.photos/501/500.jpg",
            )
        )
        dogDatabase.dogDao().insertHuman(
            HumanEntity(
                id = 1,
                firstName = "Florent",
                name = "Champigny",
            )
        )
        dogDatabase.dogDao().insertHuman(
            HumanEntity(
                id = 2,
                firstName = "Camille",
                name = "Champigny",
            )
        )
        dogDatabase.dogDao().insertHumanWithDogEntity(
            HumanWithDogEntity(
                humanId = 1, // florent
                dogId = 1,
            )
        )
        dogDatabase.dogDao().insertHumanWithDogEntity(
            HumanWithDogEntity(
                humanId = 2, // camille
                dogId = 1,
            )
        )

        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 2, name = "Vanille", breed = "Basset", age = 2,
                pictureUrl = "https://picsum.photos/501/501.jpg",
            )
        )
        dogDatabase.dogDao().insertHuman(
            HumanEntity(
                id = 3,
                firstName = "Auguste",
                name = "Dum",
            )
        )
        dogDatabase.dogDao().insertHumanWithDogEntity(
            HumanWithDogEntity(
                humanId = 3, // auguste
                dogId = 2,
            )
        )

        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = 3, name = "Scoubi", breed = "Yorkshire", age = 2,
                pictureUrl = "https://picsum.photos/501/502.jpg",
            )
        )
        dogDatabase.dogDao().insertHuman(
            HumanEntity(
                id = 4,
                firstName = "Jean",
                name = "Paul",
            )
        )
        dogDatabase.dogDao().insertHumanWithDogEntity(
            HumanWithDogEntity(
                humanId = 4, // auguste
                dogId = 3,
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
                    pictureUrl = "https://picsum.photos/500/50${i%10}.jpg",
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