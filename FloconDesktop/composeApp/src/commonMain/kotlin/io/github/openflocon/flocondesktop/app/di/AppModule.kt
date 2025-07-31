package io.github.openflocon.flocondesktop.app.di

import com.florent37.flocondesktop.app.InitialSetupStateHolder
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayer
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayerHandler
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayerImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        includes(appUiModule)

        singleOf(::FeedbackDisplayerImpl) {
            bind<FeedbackDisplayer>()
            bind<FeedbackDisplayerHandler>()
        }

        singleOf(::InitialSetupStateHolder)
    }
