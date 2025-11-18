@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocon.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.myapplication.dashboard.initializeDashboard
import io.github.openflocon.flocon.myapplication.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.deeplinks.initializeDeeplinks
import io.github.openflocon.flocon.myapplication.graphql.GraphQlTester
import io.github.openflocon.flocon.myapplication.grpc.GrpcController
import io.github.openflocon.flocon.myapplication.images.initializeImages
import io.github.openflocon.flocon.myapplication.sharedpreferences.initializeSharedPreferences
import io.github.openflocon.flocon.myapplication.sharedpreferences.initializeSharedPreferencesAfterInit
import io.github.openflocon.flocon.myapplication.table.initializeTable
import io.github.openflocon.flocon.myapplication.ui.ImagesListView
import io.github.openflocon.flocon.myapplication.ui.theme.MyApplicationTheme
import io.github.openflocon.flocon.okhttp.FloconOkhttpInterceptor
import io.github.openflocon.flocon.plugins.analytics.analytics
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.plugins.analytics.model.analyticsProperty
import io.github.openflocon.flocon.plugins.tables.model.toParam
import io.github.openflocon.flocon.plugins.tables.table
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.uuid.Uuid
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent.data?.let {
            Toast.makeText(this, "opend with : $it", Toast.LENGTH_LONG).show()
        }

        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(FloconOkhttpInterceptor(isImage = {
                it.request.url.toString().contains("picsum")
            }))
            .build()

        initializeSharedPreferences(applicationContext)
        initializeDatabases(context = applicationContext)

        FloconLogger.enabled = true
        Flocon.initialize(this)
        initializeDeeplinks()

        initializeSharedPreferencesAfterInit(applicationContext)

        val dummyHttpCaller = DummyHttpCaller(client = okHttpClient)
        val dummyWebsocketCaller = DummyWebsocketCaller(client = okHttpClient)
        GlobalScope.launch { dummyWebsocketCaller.connectToWebsocket() }
        val graphQlTester = GraphQlTester(client = okHttpClient)
        initializeImages(context = this, okHttpClient = okHttpClient)
        initializeDashboard(this)
        initializeTable(this)

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.fillMaxSize().padding(innerPadding)) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Button(
                                onClick = {
                                    dummyHttpCaller.call()
                                }
                            ) {
                                Text("okhttp test")
                            }
                            Button(
                                onClick = {
                                    dummyHttpCaller.callGzip()
                                }
                            ) {
                                Text("okhttp gzip test")
                            }
                            Button(
                                onClick = {
                                    GlobalScope.launch {
                                        graphQlTester.fetchViewerInfo()
                                    }
                                }
                            ) {
                                Text("graphql test")
                            }
                            Button(
                                onClick = {
                                    GlobalScope.launch {
                                        GrpcController.sayHello()
                                    }
                                }
                            ) {
                                Text("grpc test")
                            }
                            Button(
                                onClick = {
                                    DummyHttpKtorCaller.call()
                                }
                            ) {
                                Text("ktor test")
                            }
                            Button(
                                onClick = {
                                    dummyWebsocketCaller.send(Uuid.random().toString())
                                }
                            ) {
                                Text("websocket test")
                            }
                            Button(
                                onClick = {
                                    val value = Random.nextInt(from = 0, until = 1000).toString()
                                    Flocon.table("analytics").log(
                                        "name" toParam "new name $value",
                                        "value1" toParam "value1 $value",
                                        "value2" toParam "value2 $value",
                                    )
                                }
                            ) {
                                Text("send table event")
                            }
                            Button(
                                onClick = {
                                    Flocon.analytics("firebase").logEvents(
                                        AnalyticsEvent(
                                            eventName = "clicked user",
                                            "userId" analyticsProperty "1024",
                                            "username" analyticsProperty "florent",
                                            "index" analyticsProperty "3",
                                        ),
                                        AnalyticsEvent(
                                            eventName = "opened profile",
                                            "userId" analyticsProperty "2048",
                                            "username" analyticsProperty "kevin",
                                            "age" analyticsProperty "34",
                                        ),
                                    )
                                }
                            ) {
                                Text("send analytics event")
                            }
                        }

                        ImagesListView(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}