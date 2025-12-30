package io.github.openflocon.flocondesktop.features.database

import io.github.openflocon.domain.database.usecase.GetDatabaseQueryLogsUseCase
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseQueryLogsToCsvProcessor
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseQueryLogsToMarkdownProcessor
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseResultToCsvProcessor
import io.github.openflocon.flocondesktop.features.database.processor.ImportSqlQueryProcessor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val databaseModule = module {
    viewModelOf(::DatabaseViewModel)
    viewModelOf(::DatabaseTabViewModel)
    factoryOf(::DatabaseSelectorDelegate)
    factoryOf(::ImportSqlQueryProcessor)
    factoryOf(::ExportDatabaseResultToCsvProcessor)
    factoryOf(::ExportDatabaseQueryLogsToCsvProcessor)
    factoryOf(::ExportDatabaseQueryLogsToMarkdownProcessor)
    factoryOf(::GetDatabaseQueryLogsUseCase)
    viewModelOf(::DatabaseQueryLogsViewModel)
}
