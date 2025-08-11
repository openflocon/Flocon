package io.github.openflocon.flocondesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.app.AppViewModel
import io.github.openflocon.domain.common.ui.feedback.FeedbackDisplayerView
import io.github.openflocon.flocondesktop.main.ui.MainScreen
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    FloconTheme {
        val viewModel: AppViewModel = koinViewModel()

        Box(
            Modifier
                .safeContentPadding()
                .fillMaxSize()
                .background(FloconTheme.colorPalette.background),
        ) {
            MainScreen(
                modifier = Modifier
                    .fillMaxSize(),
            )
            FeedbackDisplayerView()
        }
    }
}
