plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    jvm("desktop")

    compilerOptions {
        // Pour Kotlin 1.9+
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.androidx.paging.common)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kermit)

            implementation(projects.domain)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
