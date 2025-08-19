package io.github.openflocon.flocondesktop.app.di

import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.feedback.FeedbackDisplayerHandler
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayerImpl
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
