package com.flocon.sample.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flocon.sample.android.grpc.GrpcController
import io.github.openflocon.flocon.FloconConnectionMode
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.featureflags.FloconFeatureFlags
import io.github.openflocon.flocon.crashreporter.FloconCrashReporter
import io.github.openflocon.flocon.database.core.FloconDatabase
import io.github.openflocon.flocon.database.room.room
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.Databases
import io.github.openflocon.flocon.myapplication.multi.Databases.getDogDatabase
import io.github.openflocon.flocon.myapplication.multi.Databases.getFoodDatabase
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.DummyWebsocketCaller
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.graphql.GraphQlTester
import io.github.openflocon.flocon.myapplication.multi.images.initializeImages
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.initializeDatastores
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.initializeSharedPreferences
import io.github.openflocon.flocon.myapplication.multi.tester.AndroidTestContext
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.myapplication.multi.ui.registerAndroidSampleFeatures
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.okhttp.FloconOkhttpInterceptor
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboard
import io.github.openflocon.flocon.plugins.device.FloconDevice
import io.github.openflocon.flocon.plugins.files.FloconFiles
import io.github.openflocon.flocon.plugins.sharedprefs.FloconPreferences
import io.github.openflocon.flocon.startFlocon
import io.github.openflocon.flocon.tables.FloconTable
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient

/**
 * How the sample reaches Flocon Desktop. [FloconConnectionMode.Bluetooth] needs no adb and no network:
 * see docs/bluetooth-exploration.md, the desktop connects from Settings > Connection.
 */
private val SampleConnectionMode: FloconConnectionMode = FloconConnectionMode.Adb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FloconLogger.enabled = true

        if (SampleConnectionMode == FloconConnectionMode.Bluetooth && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // the SDK can't ask for it, it retries to connect until it is granted
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 0)
        }

        startFlocon(FloconContext(this)) {
            connectionMode = SampleConnectionMode
            install(FloconDeeplinks) {
                deeplink("flocon://home")
                deeplink("flocon://test")
                deeplink("flocon://user/[userId]") {
                    label = "User"
                    "userId" withAutoComplete listOf("Florent", "David", "Guillaume")
                }
                deeplink("flocon://post/[postId]?comment=[commentText]") {
                    label = "Post"
                    description = "Open a post and send a comment"
                }
            }
            install(FloconNetwork)
            install(FloconDashboard)
            install(FloconTable)
            install(FloconAnalytics)
            install(FloconFeatureFlags)
            install(FloconDatabase) {
                room()
            }
            install(FloconPreferences)
            install(FloconFiles) {
                // shown in the Files section of the desktop app, next to Files, Caches and Databases
                folder(
                    displayName = "Shared preferences",
                    absolutePath = "${filesDir.parentFile!!.absolutePath}/shared_prefs",
                )
            }
            install(FloconDevice)
            install(FloconCrashReporter)
        }

        intent.data?.let {
            Toast.makeText(this, "opened with : $it", Toast.LENGTH_LONG).show()
        }

        // Initialize OkHttpClient with Flocon OkHttp Interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                FloconOkhttpInterceptor(
                    isImage = {
                        it.request.url.toString().contains("picsum")
                    }
                )
            )
            .build()

        AndroidTestContext.okHttpClient = okHttpClient
        AndroidTestContext.dummyWebsocketCaller = DummyWebsocketCaller(okHttpClient)
        AndroidTestContext.dummyWebsocketCaller.connectToWebsocket()
        AndroidTestContext.graphQlTester = GraphQlTester(okHttpClient, githubToken = BuildConfig.GITHUB_TOKEN)
        AndroidTestContext.inMemoryDb = Databases.getInMemoryDogDatabase(applicationContext)
        AndroidTestContext.grpcSayHello = { GrpcController.sayHello() }
        registerAndroidSampleFeatures(applicationContext)

        initializeImages(context = this, okHttpClient = okHttpClient)

        // Initialize Ktor client with Flocon plugin
        val ktorClient = HttpClient(OkHttp) {
            install(FloconKtorPlugin)
        }

        // Initialize the HTTP caller
        DummyHttpKtorCaller.initialize(ktorClient)

        initializeSharedPreferences(applicationContext)
        initializeDatastores(applicationContext)

        val dogDatabase = getDogDatabase(this)

        initializeDatabases(
            dogDatabase = dogDatabase,
            foodDatabase = getFoodDatabase(this),
        )

        setContent {
            App()
        }
    }
}