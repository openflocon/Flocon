plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
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
            implementation(libs.androidx.paging.common)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)

            implementation(projects.domain)
            implementation(projects.data.core)

            implementation(libs.opencsv)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
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
