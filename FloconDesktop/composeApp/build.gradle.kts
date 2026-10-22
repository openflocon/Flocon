import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp) // Add KSP plugin
    alias(libs.plugins.room)
    alias(libs.plugins.about.libraries)

    alias(libs.plugins.buildconfig)
}

buildConfig {
    packageName("io.github.openflocon.flocondesktop")

    buildConfigField("APP_VERSION", System.getenv("PROJECT_VERSION_NAME") ?: "1.0.0")

    // Telemetry (see docs/telemetry.md). Only official builds get a key, from the POSTHOG_API_KEY GitHub secret.
    // Without it (local builds, forks) the app still compiles and telemetry is a no-op.
    buildConfigField(
        "POSTHOG_API_KEY",
        System.getenv("POSTHOG_API_KEY")?.takeIf { it.isNotBlank() }
            ?: (findProperty("flocon.posthogApiKey") as String?)
            ?: "",
    )
    buildConfigField(
        "POSTHOG_HOST",
        System.getenv("POSTHOG_HOST")?.takeIf { it.isNotBlank() } ?: "https://eu.i.posthog.com",
    )
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-opt-in=androidx.compose.ui.ExperimentalComposeUiApi")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            // TODO Remove
            implementation(compose.components.resources)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.coroutines.core)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
            implementation(libs.sqlite.bundled)
            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.m3) // Material 3
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.markdown.renderer)

            // TODO Remove
            implementation(projects.data.remote)
            implementation(projects.data.local)
            implementation(projects.data.core)

            implementation(projects.domain)
            implementation(projects.mcp)

            implementation(projects.library.designsystem)

            implementation(projects.navigation)

            implementation(libs.kermit)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)

//            implementation(libs.material3.adaptive)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
            implementation(libs.ui.tooling.preview.desktop)
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.desktop {
    application {
        mainClass = "io.github.openflocon.flocondesktop.MainKt"

        buildTypes.release {
            proguard {
                // Active ProGuard
                isEnabled.set(true)

                configurationFiles.from(file("proguard-rules.pro"))
            }
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "Flocon"
            packageVersion = System.getenv("PROJECT_VERSION_NAME") ?: "1.0.0"
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/files/flocon_big.icns"))
                bundleID = "io.github.openflocon.flocon"
                dockName = "Flocon"
            }
            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon_small.png"))
            }
            windows {
                iconFile.set(project.file("src/desktopMain/resources/files/flocon_big.ico"))
                menu = true
                upgradeUuid = "5c6d2b4c-360a-4135-a445-68bfa25ce450"
            }
        }
    }
}
