package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.dashboard.initializeDashboard
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.plugins.analytics.floconAnalytics
import io.github.openflocon.flocon.plugins.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.plugins.analytics.model.analyticsProperty
import io.github.openflocon.flocon.plugins.tables.floconTable
import io.github.openflocon.flocon.plugins.tables.model.toParam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun App() {
    LaunchedEffect(Unit) {
        initializeDashboard()
    }
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Flocon Multi App",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            DummyHttpKtorCaller.callGet()
                        }
                    ) {
                        Text("Ktor GET test")
                    }
                    Button(
                        onClick = {
                            DummyHttpKtorCaller.callPost()
                        }
                    ) {
                        Text("Ktor POST test")
                    }
                    Button(
                        onClick = {
                            val value = Random.nextInt(from = 0, until = 1000).toString()
                            floconTable("analytics").log(
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
                            floconAnalytics("firebase").logEvents(
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

                    ImagesListView(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

