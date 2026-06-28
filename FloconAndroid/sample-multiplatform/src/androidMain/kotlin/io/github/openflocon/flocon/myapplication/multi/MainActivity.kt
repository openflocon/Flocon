package io.github.openflocon.flocon.myapplication.multi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.Databases.getDogDatabase
import io.github.openflocon.flocon.myapplication.multi.Databases.getFoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.initializeSharedPreferences
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.initializeDatastores
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.github.openflocon.flocon.deeplinks.FloconDeeplinks
import io.github.openflocon.flocon.startFlocon
import io.github.openflocon.flocon.okhttp.FloconOkhttpInterceptor
import io.github.openflocon.flocon.analytics.FloconAnalytics
import io.github.openflocon.flocon.database.core.FloconDatabase
import io.github.openflocon.flocon.database.room.room
import io.github.openflocon.flocon.network.core.FloconNetwork
import io.github.openflocon.flocon.tables.FloconTable
import io.github.openflocon.flocon.myapplication.multi.images.initializeImages
import io.github.openflocon.flocon.myapplication.multi.graphql.GraphQlTester
import io.github.openflocon.flocon.myapplication.multi.database.DogDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient

object AndroidTestContext {
    lateinit var okHttpClient: OkHttpClient
    lateinit var dummyHttpCaller: DummyHttpCaller
    lateinit var dummyWebsocketCaller: DummyWebsocketCaller
    lateinit var graphQlTester: GraphQlTester
    lateinit var inMemoryDb: DogDatabase
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FloconLogger.enabled = true

        startFlocon(FloconContext(this)) {
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
            install(FloconTable)
            install(FloconAnalytics)
            install(FloconDatabase) {
                room()
            }
        }

        intent.data?.let {
            Toast.makeText(this, "opened with : $it", Toast.LENGTH_LONG).show()
        }

        // Initialize OkHttpClient with Flocon OkHttp Interceptor
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(
                FloconOkhttpInterceptor(
                    isImage = {
                        it.request.url.toString().contains("picsum")
                    }
                )
            )
            .build()

        AndroidTestContext.okHttpClient = okHttpClient
        AndroidTestContext.dummyHttpCaller = DummyHttpCaller(okHttpClient)
        AndroidTestContext.dummyWebsocketCaller = DummyWebsocketCaller(okHttpClient)
        AndroidTestContext.dummyWebsocketCaller.connectToWebsocket()
        AndroidTestContext.graphQlTester = GraphQlTester(okHttpClient)
        AndroidTestContext.inMemoryDb = Databases.getInMemoryDogDatabase(applicationContext)

        initializeImages(context = this, okHttpClient = okHttpClient)

        // Initialize Ktor client with Flocon plugin
        val ktorClient = HttpClient(OkHttp) {
            install(FloconKtorPlugin) {
                /*
                shouldLog = {
                    val url = it.url.toString()
                    println("url: $url")
                    url.contains("1").not()
                }
                 */
            }
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


