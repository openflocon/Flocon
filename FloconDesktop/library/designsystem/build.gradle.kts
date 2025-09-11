plugins {
    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeHotReload)
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

                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(compose.ui)
                api(compose.components.resources)
                api(compose.components.uiToolingPreview)
                api(libs.other.jsontree)

                api(libs.androidx.navigation3.ui)
                api(libs.androidx.navigation3.runtime)

                // Not KMP yet
//                api(libs.androidx.lifecycle.viewmodel.navigation3)
                api(libs.kotlinx.serialization.core)
                api("com.composables:core:1.43.1")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

}
