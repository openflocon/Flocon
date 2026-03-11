package io.github.openflocon.flocon

import kotlinx.coroutines.flow.StateFlow

abstract class FloconApp {
    lateinit var context: FloconContext

    companion object {
        var instance: FloconApp? = null
            private set
    }

    interface Client {

        @Throws(Throwable::class)
        suspend fun connect(onClosed: () -> Unit)
        suspend fun disconnect()

//        val databasePlugin: FloconDatabasePlugin?
//        val dashboardPlugin: FloconDashboardPlugin?
//        val tablePlugin: FloconTablePlugin?
        //val deeplinksPlugin: FloconDeeplinksPlugin?
//        val analyticsPlugin: FloconAnalyticsPlugin?
//        val networkPlugin: FloconNetworkPlugin?
//        val devicePlugin: FloconDevicePlugin?
//        val preferencesPlugin: FloconPreferencesPlugin?
//        val crashReporterPlugin: FloconCrashReporterPlugin?

        /**
         * Retrieve a plugin instance by its [key].
         */
        fun <T : FloconPlugin> getPlugin(key: String): T?
    }

    open val client: Client? = null

    abstract val isInitialized: StateFlow<Boolean>

//    protected fun initializeFlocon(context: FloconContext) {
//        this.context = context
//        instance = this
//    }

}