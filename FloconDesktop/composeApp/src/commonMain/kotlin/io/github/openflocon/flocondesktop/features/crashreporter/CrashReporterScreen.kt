package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrashReporterScreen(
    modifier: Modifier = Modifier,
    viewModel: CrashReporterViewModel = koinViewModel()
) {
    val crashReports by viewModel.crashReports.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(crashReports) { crash ->
            CrashReportItem(crash)
        }
    }
}

@Composable
fun CrashReportItem(crash: CrashReportDomainModel) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = crash.exceptionType,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = crash.exceptionMessage,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
