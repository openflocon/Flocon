package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.analytics.analyticsPlugin
import io.github.openflocon.flocon.analytics.model.AnalyticsEvent
import io.github.openflocon.flocon.analytics.model.analyticsProperty
import io.github.openflocon.flocon.myapplication.multi.DummyHttpKtorCaller
import io.github.openflocon.flocon.myapplication.multi.dashboard.initializeDashboard
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
                            Random.nextInt(from = 0, until = 1000).toString()
                            Flocon.analyticsPlugin.log(
                                AnalyticsEvent(
                                    eventName = "clicked user",
                                    analyticsTableId = "analytics",
                                    properties = listOf(
                                        "userId" analyticsProperty "1024",
                                        "username" analyticsProperty "florent",
                                        "index" analyticsProperty "3"
                                    )
                                )
                            )
                        }
                    ) {
                        Text("send table event")
                    }
                    Button(
                        onClick = {
                            Flocon.analyticsPlugin
                                .log(
                                    listOf(
                                        AnalyticsEvent(
                                            eventName = "clicked user",
                                            analyticsTableId = "firebase",
                                            properties = listOf(
                                                "userId" analyticsProperty "1024",
                                                "username" analyticsProperty "florent",
                                                "index" analyticsProperty "3"
                                            )
                                        ),
                                        AnalyticsEvent(
                                            eventName = "opened profile",
                                            analyticsTableId = "firebase",
                                            properties = listOf(
                                                "userId" analyticsProperty "2048",
                                                "username" analyticsProperty "kevin",
                                                "age" analyticsProperty "34"
                                            )
                                        )
                                    )
                                )
                        }
                    ) {
                        Text("send analytics event")
                    }

                    PlatformSpecificTests(modifier = Modifier.fillMaxWidth())

                    ImagesListView(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
expect fun PlatformSpecificTests(modifier: Modifier = Modifier)


