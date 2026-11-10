@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.myapplication.multi.ui

import android.content.Context
import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import io.github.openflocon.flocon.myapplication.multi.Databases
import io.github.openflocon.flocon.myapplication.multi.database.newFood
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanEntity
import io.github.openflocon.flocon.myapplication.multi.database.model.HumanWithDogEntity
import io.github.openflocon.flocon.myapplication.multi.room2.CatDatabase
import io.github.openflocon.flocon.myapplication.multi.room2.CatEntity
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.updateDatastoreAge
import io.github.openflocon.flocon.myapplication.multi.tester.AndroidTestContext
import io.github.openflocon.flocon.myapplication.multi.tester.FloconOkHttpTester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Android supports every feature of the sample. [AndroidTestContext] must be filled first.
 */
fun registerAndroidSampleFeatures(context: Context) {
    SampleFeatures.platform = SamplePlatform.Android

    SampleFeatures.on(SampleFeature.OkHttp) { FloconOkHttpTester.call() }
    SampleFeatures.on(SampleFeature.OkHttpGzip) { FloconOkHttpTester.callGzip() }
    SampleFeatures.on(SampleFeature.WebSocket) {
        AndroidTestContext.dummyWebsocketCaller.send(Uuid.random().toString())
    }
    SampleFeatures.on(SampleFeature.GraphQl) { AndroidTestContext.graphQlTester.fetchViewerInfo() }
    SampleFeatures.on(SampleFeature.Grpc) { AndroidTestContext.grpcSayHello() }
    SampleFeatures.on(SampleFeature.DatabaseRoom3) {
        Databases.getDogDatabase(context).dogDao().insertDog(newDog("Flocon"))
    }
    SampleFeatures.on(SampleFeature.DatabaseFood) {
        Databases.getFoodDatabase(context).foodDao().insertFood(newFood())
    }
    SampleFeatures.on(SampleFeature.DatabaseRoom2) {
        CatDatabase.get(context).catDao().insertCat(
            CatEntity(id = System.currentTimeMillis(), name = "Flocon cat", age = Random.nextInt(1, 15))
        )
    }
    SampleFeatures.on(SampleFeature.DatabaseTransactionRoom3) {
        // the three inserts and the transaction statements share one transactionId in the query logs
        val database = Databases.getDogDatabase(context)
        val dao = database.dogDao()
        val dog = newDog("Transaction dog")
        val human = HumanEntity(id = dog.id, firstName = "Transaction", name = "Human")
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                dao.insertDog(dog)
                dao.insertHuman(human)
                dao.insertHumanWithDogEntity(HumanWithDogEntity(humanId = human.id, dogId = dog.id))
            }
        }
    }
    SampleFeatures.on(SampleFeature.DatabaseTransactionRoom2) {
        val database = CatDatabase.get(context)
        withContext(Dispatchers.IO) {
            database.runInTransaction {
                val now = System.currentTimeMillis()
                repeat(3) { index ->
                    database.catDao().insertCatBlocking(
                        CatEntity(id = now + index, name = "Transaction cat $index", age = Random.nextInt(1, 15)),
                    )
                }
            }
        }
    }
    SampleFeatures.on(SampleFeature.DatabaseNestedTransactionRoom3) {
        // transaction { transaction { insertUsers() }; transaction { insertPosts(); insertComments() } }
        // Room 3 runs the inner transactions as savepoints of the outer one
        val database = Databases.getDogDatabase(context)
        val dao = database.dogDao()
        val dog = newDog("Nested transaction dog")
        val human = HumanEntity(id = dog.id, firstName = "Nested", name = "Human")
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                withNestedTransaction {
                    dao.insertHuman(human)
                }
                withNestedTransaction {
                    dao.insertDog(dog)
                    dao.insertHumanWithDogEntity(HumanWithDogEntity(humanId = human.id, dogId = dog.id))
                }
            }
        }
    }
    SampleFeatures.on(SampleFeature.DatabaseNestedTransactionRoom2) {
        // Room 2 only sends the BEGIN of the outer transaction, the inner ones are part of it
        val database = CatDatabase.get(context)
        withContext(Dispatchers.IO) {
            database.runInTransaction {
                val now = System.currentTimeMillis()
                database.runInTransaction {
                    database.catDao().insertCatBlocking(CatEntity(id = now, name = "Nested cat 0", age = Random.nextInt(1, 15)))
                }
                database.runInTransaction {
                    repeat(2) { index ->
                        database.catDao().insertCatBlocking(
                            CatEntity(id = now + 1 + index, name = "Nested cat ${index + 1}", age = Random.nextInt(1, 15)),
                        )
                    }
                }
            }
        }
    }
    SampleFeatures.on(SampleFeature.DatabaseInMemory) {
        AndroidTestContext.inMemoryDb.dogDao().insertDog(newDog("InMemory Flocon"))
    }
    SampleFeatures.on(SampleFeature.DataStore) {
        updateDatastoreAge(context, Random.nextInt(100))
    }
    SampleFeatures.on(SampleFeature.Files) {
        File(context.filesDir, "flocon_sample.txt").writeText("Written by the sample at ${System.currentTimeMillis()}")
    }
}

private fun newDog(name: String) = DogEntity(
    id = System.currentTimeMillis(),
    name = name,
    breed = "Golden Retriever ${System.currentTimeMillis()}",
    age = 6,
    pictureUrl = "https://picsum.photos/501/500.jpg",
)
