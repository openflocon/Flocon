plugins {
    id("flocon.kotlin.library")
    id("flocon.android.library")
    id("flocon.jvm.library")
    id("flocon.ios.library")
    id("flocon.wasm.library")

    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.apollo)
}

kotlin {
    android {
        namespace = "io.github.openflocon.flocon.myapplication.multi"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.flocon)
            implementation(projects.deeplinks)
            implementation(projects.analytics)
            implementation(projects.accessibility)
            implementation(projects.featureflags)
            implementation(projects.tables)
            implementation(projects.sharedprefs)
            implementation(projects.network.ktorInterceptor)
            implementation(projects.database.room3)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // Ktor client core
            implementation(libs.ktor.client.core)

            // room
            implementation(libs.androidx.room3.runtime)

            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.compose.navigation3.ui)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Apollo (GraphQL)
            implementation(libs.apollo.runtime)

        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)

            // Ktor client for Android
            implementation(libs.ktor.client.okhttp)

            // OkHttp
            implementation(libs.okhttp)

            // Coil with OkHttp network fetcher
            implementation(libs.coil.network.okhttp)

            // Datastore
            implementation(libs.androidx.datastore.preferences)

            implementation(projects.network.ktorInterceptor)
            implementation(projects.network.okhttpInterceptor)
            implementation(projects.database.room3)
            implementation(projects.database.room)
            implementation(libs.androidx.room.runtime)
            implementation(projects.sharedprefs)
            implementation(projects.datastores)
            implementation(projects.analytics)
            implementation(projects.crashreporter)
            implementation(projects.tables)
        }

    }
}

dependencies {
    listOf(
        "kspAndroid",
        "kspJvm",
        "kspIosSimulatorArm64",
        "kspIosArm64",
        "kspWasmJs"
    ).forEach {
        add(it, libs.androidx.room3.compiler)
    }
    // Room 2, used by an Android only database to test the Room 2 module
    add("kspAndroid", libs.androidx.room.compiler)

}

apollo {
    service("github") {
        packageName.set("com.github")
        srcDir("src/androidMain/graphql")
    }
}
