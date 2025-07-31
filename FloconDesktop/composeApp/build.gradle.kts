import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp) // Add KSP plugin
    alias(libs.plugins.room)
    alias(libs.plugins.ktlint) // Ajout de Ktlint ici
    alias(libs.plugins.aboutLibraries)
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
    }

    androidTarget()

    // listOf(
    //    iosX64(),
    //    iosArm64(),
    //    iosSimulatorArm64()
    // ).forEach { iosTarget ->
    //    iosTarget.binaries.framework {
    //        baseName = "ComposeApp"
    //        isStatic = true
    //    }
    // }

    jvm("desktop")

    // @OptIn(ExperimentalWasmDsl::class)
    // wasmJs {
    //    outputModuleName.set("composeApp")
    //    browser {
    //        val rootDirPath = project.rootDir.path
    //        val projectDirPath = project.projectDir.path
    //        commonWebpackConfig {
    //            outputFileName = "composeApp.js"
    //            devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
    //                static = (static ?: mutableListOf()).apply {
    //                    // Serve sources to debug inside browser
    //                    add(rootDirPath)
    //                    add(projectDirPath)
    //                }
    //            }
    //        }
    //    }
    //    binaries.executable()
    // }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
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
            implementation(libs.sqlite.bundled)
            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.m3) // Material 3
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.clientJava)
        }
    }
}

android {
    namespace = "com.florent37.flocondesktop"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.florent37.flocondesktop"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Room schema location for Android (if not using KSP argument)
    // You might need to configure KSP arguments for Room schema location
    // defaultConfig {
    //     javaCompileOptions {
    //         annotationProcessorOptions {
    //             arguments["room.schemaLocation"] = "$projectDir/schemas".toString()
    //         }
    //     }
    // }
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
    filter {
        exclude { element ->
            val path = element.file.path
            path.contains("/generated/")
        }
        include("**/kotlin/**")
        exclude("**/generated/**")
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
    debugImplementation(compose.uiTooling)
    ktlintRuleset("io.nlopez.compose.rules:ktlint:0.4.24")
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.desktop {
    application {
        mainClass = "com.florent37.flocondesktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Flocon"
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/files/flocon_big.icns"))
            }
        }
    }
}
