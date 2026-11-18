package io.github.openflocon.flocondesktop.app.di

import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.feedback.FeedbackDisplayerHandler
import io.github.openflocon.domain.log.LogManager
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.app.MenuNavigator
import io.github.openflocon.flocondesktop.app.capture.WindowCaptureController
import io.github.openflocon.flocondesktop.common.log.LogManagerEditable
import io.github.openflocon.flocondesktop.common.log.LogManagerImpl
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerImpl
import io.github.openflocon.mcp.capture.FloconWindowCapturer
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    includes(appUiModule)

    singleOf(::FeedbackDisplayerImpl) {
        bind<FeedbackDisplayer>()
        bind<FeedbackDisplayerHandler>()
    }

    singleOf(::WindowCaptureController) {
        bind<FloconWindowCapturer>()
    }

    singleOf(::InitialSetupStateHolder)
    singleOf(::MenuNavigator)
    singleOf(::LogManagerImpl) {
        bind<LogManager>()
        bind<LogManagerEditable>()
    }
}
