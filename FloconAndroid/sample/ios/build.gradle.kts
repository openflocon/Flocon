plugins {
    id("flocon.kotlin.library")
    id("flocon.ios.library")

    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp" // Swift does `import ComposeApp`
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(projects.sampleMultiplatform)

            implementation(projects.flocon.flocon)
            implementation(projects.deeplinks.deeplinks)
            implementation(projects.analytics.analytics)
            implementation(projects.accessibility.accessibility)
            implementation(projects.featureflags.featureflags)
            implementation(projects.tables.tables)
            implementation(projects.sharedprefs.sharedprefs)
            implementation(projects.files.files)
            implementation(projects.device.device)
            implementation(projects.crashreporter.crashreporter)
            implementation(projects.network.ktorInterceptor.ktorInterceptor)
            implementation(projects.database.room3.room3)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.darwin)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.sqlite.framework) // NativeSQLiteDriver
        }
    }
}
