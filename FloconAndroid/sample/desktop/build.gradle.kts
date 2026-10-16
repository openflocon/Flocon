plugins {
    id("flocon.kotlin.library")
    id("flocon.jvm.library")

    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.sampleMultiplatform)

            implementation(projects.flocon)
            implementation(projects.deeplinks)
            implementation(projects.analytics)
            implementation(projects.network.ktorInterceptor)
            implementation(projects.database.room3)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.desktop.currentOs)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.sqlite.jdbc)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.flocon.sample.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "FloconSampleDesktop"
            packageVersion = "1.0.0"
        }
    }
}
