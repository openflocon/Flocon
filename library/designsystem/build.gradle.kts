plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                api("org.jetbrains.compose.runtime:runtime:1.12.2")
                api("org.jetbrains.compose.foundation:foundation:1.12.2")
                api("org.jetbrains.compose.animation:animation:1.12.2")
                api("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
                api("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                api("org.jetbrains.compose.ui:ui:1.12.2")
                api("org.jetbrains.compose.components:components-resources:1.12.2")
                api("org.jetbrains.compose.ui:ui-tooling-preview:1.12.2")
                api(libs.other.jsontree)
                api(libs.kotlinx.immutable)

                api(libs.compose.navigation3.ui)

                // Not KMP yet
//                api(libs.androidx.lifecycle.viewmodel.navigation3)
                api(libs.kotlinx.serialization.core)
                api("com.composables:core:1.43.1")
                api(libs.filekit.core)
                api(libs.filekit.dialogs)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
