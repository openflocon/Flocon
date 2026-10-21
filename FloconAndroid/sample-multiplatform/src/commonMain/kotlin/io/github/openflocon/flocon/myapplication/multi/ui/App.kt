package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.dashboard.initializeDashboard
import io.github.openflocon.flocon.myapplication.multi.featureflags.initializeFeatureFlags
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App() {
    LaunchedEffect(Unit) {
        initializeDashboard()
        initializeFeatureFlags()
        SampleFeatures.registerPreferences()
    }
    val scope = rememberCoroutineScope()

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
                    text = "Flocon Multi App - ${SampleFeatures.platformName}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SampleFeature.entries.groupBy { it.group }.forEach { (group, features) ->
                        Text(text = group, style = MaterialTheme.typography.titleSmall)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            features.forEach { feature ->
                                when (val status = SampleFeatures.status(feature)) {
                                    is SampleFeatureStatus.Available -> Button(
                                        onClick = { scope.launch { status.action() } },
                                    ) {
                                        Text(feature.title)
                                    }

                                    // a disabled button is displayed in gray
                                    is SampleFeatureStatus.Unavailable -> Button(
                                        onClick = {},
                                        enabled = false,
                                    ) {
                                        Text(feature.title)
                                    }
                                }
                            }
                        }
                        features.forEach { feature ->
                            (SampleFeatures.status(feature) as? SampleFeatureStatus.Unavailable)?.let {
                                Text(
                                    text = "${feature.title}: ${it.reason}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }

                ImagesListView(modifier = Modifier.fillMaxWidth().weight(1f))
            }
        }
    }
}
