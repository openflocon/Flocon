plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    androidTarget()

    jvm("desktop")

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.android)
        }

        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(compose.ui)
                api(compose.components.resources)
                api(compose.components.uiToolingPreview)

                // Maybe have to change since Google push back some component to 1.5.0
                api("org.jetbrains.compose.material3:material3:1.9.0-alpha04")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

}

android {
    namespace = "io.github.openflocon.library.designsystem"
    compileSdk = libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk = libs.versions.android.targetSdk
                .get()
                .toInt()
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
}
