plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain(21)

    jvm("desktop")

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kermit)

            implementation(projects.domain)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.mcp.sdk.server)
        }

        getByName("desktopTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.server.test.host)
            implementation(libs.mcp.sdk.client)
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.paging.common)
        }
    }
}
