package io.github.openflocon.flocon.myapplication.multi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.ktor.FloconKtorPlugin
import io.github.openflocon.flocon.myapplication.multi.Databases.getDogDatabase
import io.github.openflocon.flocon.myapplication.multi.Databases.getFoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.FoodDatabase
import io.github.openflocon.flocon.myapplication.multi.database.initializeDatabases
import io.github.openflocon.flocon.myapplication.multi.sharedpreferences.initializeSharedPreferences
import io.github.openflocon.flocon.myapplication.multi.ui.App
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent.data?.let {
            Toast.makeText(this, "opened with : $it", Toast.LENGTH_LONG).show()
        }

        // Initialize Ktor client with Flocon plugin
        val ktorClient = HttpClient(OkHttp) {
            install(FloconKtorPlugin)
        }

        // Initialize the HTTP caller
        DummyHttpKtorCaller.initialize(ktorClient)

        initializeSharedPreferences(applicationContext)
        initializeDatabases(
            dogDatabase = getDogDatabase(this),
            foodDatabase = getFoodDatabase(this),
        )

        FloconLogger.enabled = true
        Flocon.initialize(this)

        setContent {
            App()
        }
    }
}

