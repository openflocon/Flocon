import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
//    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp) // Add KSP plugin
    alias(libs.plugins.room)
    alias(libs.plugins.aboutLibraries)

    alias(libs.plugins.buildconfig)
}

buildConfig {
    packageName("io.github.openflocon.flocondesktop")

    buildConfigField("APP_VERSION", System.getenv("PROJECT_VERSION_NAME") ?: "1.0.0")
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            // TODO Remove
            implementation(compose.components.resources)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutinesCore)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.kotlinx.dateTime)
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

            implementation(projects.library.designsystem)

            implementation(projects.navigation)

            implementation(libs.kermit)

//            implementation(libs.material3.adaptive)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.clientJava)
            // implementation(libs.ui.tooling.preview.desktop)
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
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
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
            }
        }
    }
}
