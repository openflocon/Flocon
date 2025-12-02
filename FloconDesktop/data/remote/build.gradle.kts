plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.ktlint)
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

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kermit)

            implementation(projects.domain)
            implementation(projects.data.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.ktor.serverCore)
            implementation(libs.ktor.serverNetty)
            implementation(libs.ktor.serializationKotlinJson)
            implementation(libs.ktor.serverContentNegociation)
            implementation(libs.ktor.serverWebsocket)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.serializationJson)

            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientCio)
            implementation(libs.ktor.clientContentNegociation)
        }
    }
}

ktlint {
    android.set(false)
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
