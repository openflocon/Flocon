@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.flocon.sample.ios

import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSDate
import platform.Foundation.create
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile

fun registerIosSampleFeatures(dogDatabase: DogDatabase) {
    SampleFeatures.platformName = "iOS"

    SampleFeatures.on(SampleFeature.Database) {
        val now = (NSDate().timeIntervalSince1970 * 1000).toLong()
        dogDatabase.dogDao().insertDog(
            DogEntity(
                id = now,
                name = "Flocon",
                breed = "Golden Retriever $now",
                age = 6,
                pictureUrl = "https://picsum.photos/501/500.jpg",
            )
        )
    }
    SampleFeatures.on(SampleFeature.Files) {
        NSString.create(string = "Written by the sample at ${NSDate().timeIntervalSince1970}")
            .writeToFile(
                path = "${documentDirectory()}/flocon_sample.txt",
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null,
            )
    }

    SampleFeatures.unavailable(SampleFeature.OkHttp, "The OkHttp interceptor is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.OkHttpGzip, "The OkHttp interceptor is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.WebSocket, "The OkHttp WebSocket logging is Android and JVM only")
    SampleFeatures.unavailable(SampleFeature.GraphQl, "Android only in this sample")
    SampleFeatures.unavailable(SampleFeature.Grpc, "The gRPC interceptor is Android only")
    SampleFeatures.unavailable(SampleFeature.DatabaseInMemory, "Android only in this sample")
    SampleFeatures.unavailable(SampleFeature.DataStore, "DataStore is not set up in the iOS sample")
}
