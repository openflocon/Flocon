package io.github.openflocon.data.core.timelinelog

import io.github.openflocon.data.core.timelinelog.repository.TimelineLogRepositoryImpl
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.timelinelog.repository.TimelineLogRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val timelineLogModule = module {
    singleOf(::TimelineLogRepositoryImpl) {
        bind<TimelineLogRepository>()
        bind<MessagesReceiverRepository>()
    }
}
