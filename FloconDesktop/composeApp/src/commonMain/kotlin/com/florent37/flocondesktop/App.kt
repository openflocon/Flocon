package com.florent37.flocondesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.florent37.flocondesktop.app.AppViewModel
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayerView
import com.florent37.flocondesktop.main.ui.MainScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    FloconTheme {
        val viewModel: AppViewModel = koinViewModel()

        Box(
            Modifier
                .safeContentPadding()
                .fillMaxSize()
                .background(FloconColors.background),
        ) {
            MainScreen(
                modifier =
                Modifier
                    .fillMaxSize(),
            )
            FeedbackDisplayerView()
        }
    }
}
