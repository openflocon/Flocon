import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flocon.kotlin.multiplatform")
    alias(libs.plugins.kotlin.serialization)
    id("flocon.publish")
    alias(libs.plugins.buildconfig)
}


kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.jakewharton.process.phoenix)
                implementation("com.squareup.okhttp3:okhttp:4.12.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)

                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.darwin)

                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)

                // to store the device id
                implementation("com.russhwolf:multiplatform-settings:1.3.0")
                implementation(libs.androidx.sqlite.bundled)
            }
        }
    }
}

buildConfig {
    packageName("io.github.openflocon.flocondesktop")

    buildConfigField("APP_VERSION", System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String)
}

android {
    namespace = "io.github.openflocon.flocon"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}

mavenPublishing {
    coordinates(
        groupId = project.property("floconGroupId") as String,
        artifactId = "flocon",
        version = System.getenv("PROJECT_VERSION_NAME") ?: project.property("floconVersion") as String
    )
}