@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    id("flocon.kotlin.library")
    id("flocon.wasm.library")

    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    wasmJs {
        outputModuleName.set("sampleWasm")
        browser {
            commonWebpackConfig {
                outputFileName = "sampleWasm.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(projects.sampleMultiplatform)

            implementation(projects.flocon)
            implementation(projects.deeplinks)
            implementation(projects.analytics)
            implementation(projects.accessibility)
            implementation(projects.featureflags)
            implementation(projects.tables)
            implementation(projects.sharedprefs)
            implementation(projects.database.room3)
            implementation(projects.network.ktorInterceptor)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Room 3 on the web: SQLite runs in a Web Worker (see ./worker)
            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.sqlite.web)
            implementation(libs.kotlinx.browser)
            implementation(npm("sqlite-wasm-worker", file("worker")))
        }
    }
}
