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
            implementation(projects.accessibility.accessibility)
            implementation(projects.featureflags.featureflags)
            implementation(projects.timeline.timeline)
            implementation(projects.tables.tables)
            implementation(projects.crashreporter.crashreporter)
            implementation(projects.network.ktorInterceptor.ktorInterceptor)
            implementation(projects.database.room3.room3)
            implementation(projects.database.sqldelight.sqldelight)
            implementation(libs.sqldelight.native.driver)

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
