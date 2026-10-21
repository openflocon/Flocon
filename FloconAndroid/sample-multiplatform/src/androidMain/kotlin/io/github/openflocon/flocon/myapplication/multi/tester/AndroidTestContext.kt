package io.github.openflocon.flocon.myapplication.multi.tester

import io.github.openflocon.flocon.myapplication.multi.DummyWebsocketCaller
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.github.openflocon.flocon.myapplication.multi.graphql.GraphQlTester
import okhttp3.OkHttpClient

/**
 * Objects created by the Android app, used by [io.github.openflocon.flocon.myapplication.multi.ui.registerAndroidSampleFeatures]
 */
object AndroidTestContext {
    lateinit var okHttpClient: OkHttpClient
    lateinit var dummyWebsocketCaller: DummyWebsocketCaller
    lateinit var graphQlTester: GraphQlTester
    lateinit var inMemoryDb: DogDatabase

    // the gRPC stubs are generated in the app module (the protobuf plugin does not support KMP modules)
    var grpcSayHello: suspend () -> String? = { null }
}
