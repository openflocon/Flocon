@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.myapplication.multi.ui

import android.content.Context
import io.github.openflocon.flocon.myapplication.multi.Databases
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.updateDatastoreAge
import io.github.openflocon.flocon.myapplication.multi.tester.AndroidTestContext
import io.github.openflocon.flocon.myapplication.multi.tester.FloconOkHttpTester
import java.io.File
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Android supports every feature of the sample. [AndroidTestContext] must be filled first.
 */
fun registerAndroidSampleFeatures(context: Context) {
    SampleFeatures.platformName = "Android"

    SampleFeatures.on(SampleFeature.OkHttp) { FloconOkHttpTester.call() }
    SampleFeatures.on(SampleFeature.OkHttpGzip) { FloconOkHttpTester.callGzip() }
    SampleFeatures.on(SampleFeature.WebSocket) {
        AndroidTestContext.dummyWebsocketCaller.send(Uuid.random().toString())
    }
    SampleFeatures.on(SampleFeature.GraphQl) { AndroidTestContext.graphQlTester.fetchViewerInfo() }
    SampleFeatures.on(SampleFeature.Grpc) { AndroidTestContext.grpcSayHello() }
    SampleFeatures.on(SampleFeature.Database) {
        Databases.getDogDatabase(context).dogDao().insertDog(newDog("Flocon"))
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
